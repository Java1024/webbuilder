package org.webbuilder.web.core.aop.authorize;

import com.alibaba.fastjson.JSON;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.LoggerFactory;
import org.webbuilder.utils.base.ClassUtil;
import org.webbuilder.utils.base.DateTimeUtils;
import org.webbuilder.utils.base.MD5;
import org.webbuilder.utils.base.StringUtil;
import org.webbuilder.utils.script.engine.DynamicScriptEngine;
import org.webbuilder.utils.script.engine.DynamicScriptEngineFactory;
import org.webbuilder.utils.script.engine.ExecuteResult;
import org.webbuilder.utils.storage.counter.Counter;
import org.webbuilder.web.core.FastJsonHttpMessageConverter;
import org.webbuilder.web.core.aop.logger.AccessLogger;
import org.webbuilder.web.core.authorize.annotation.Authorize;
import org.webbuilder.web.core.bean.ResponseMessage;
import org.webbuilder.web.core.exception.BusinessException;
import org.webbuilder.web.core.logger.LoggerService;
import org.webbuilder.web.core.utils.WebUtil;
import org.webbuilder.web.po.logger.LogInfo;
import org.webbuilder.web.po.user.User;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * aop方式进行授权验证
 * Created by 浩 on 2015-09-28 0028.
 */
public class AuthorizeAndLoggerAdvice {

    private String AuthorizeSuccessCounterKey = "success";
    private String AuthorizeFailedCounterKey = "fail";
    private String AuthorizeExceptionCounterKey = "exception";
    private Counter counter;
    private final Map<String, AuthorizeConfig> configCache = new ConcurrentHashMap<>();
    private org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
    private LoggerService loggerService;
    private String authorizationFailsMessage = "无访问权限";

    protected String getMethodName(ProceedingJoinPoint pjp) {
        StringBuilder methodName = new StringBuilder(pjp.getSignature().getName()).append("(");
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        String[] names = signature.getParameterNames();
        Class[] args = signature.getParameterTypes();
        for (int i = 0, len = args.length; i < len; i++) {
            if (i != 0) methodName.append(",");
            methodName.append(args[i].getSimpleName()).append(" ").append(names[i]);
        }
        return methodName.append(")").toString();
    }

    private void initAuthConfig(AuthorizeConfig config, Authorize authorize) {
        if (authorize != null) {
            config.getRoles().addAll(Arrays.asList(authorize.role()));
            config.getLevel().addAll(Arrays.asList(authorize.level()));
            config.getModules().addAll(Arrays.asList(authorize.module()));
            if (!StringUtil.isNullOrEmpty(authorize.expression())) {
                String scriptId = StringUtil.concat("author_script_", authorize.expression().hashCode());
                DynamicScriptEngine engine = DynamicScriptEngineFactory.getEngine(authorize.expressionLanguage());
                try {
                    if (!engine.compiled(scriptId)) {
                        engine.compile(scriptId, authorize.expression());
                    }
                } catch (Exception e) {
                }
                config.getExpression().add(new AuthorizeConfig.Expression(scriptId, authorize.expressionLanguage()));
            }
            if (authorize.api() != config.isApi())
                config.setApi(authorize.api());
            if (authorize.mod() != config.getMod())
                config.setMod(authorize.mod());
        }
    }

    protected boolean doAuth(ProceedingJoinPoint pjp) {
        String cacheName = pjp.getTarget().getClass().getName().concat(".").concat(getMethodName(pjp));
        AuthorizeConfig config = configCache.get(cacheName);
        MethodSignature methodSignature = ((MethodSignature) pjp.getSignature());
        Map<String, Object> root = new LinkedHashMap<>();
        String[] names = methodSignature.getParameterNames();
        Object[] args = pjp.getArgs();
        for (int i = 0; i < names.length; i++) {
            root.put(names[i], args[i]);
        }
        root.put("request", WebUtil.getHttpServletRequest());
        if (config == null) {
            Method method = methodSignature.getMethod();
            Class<?> controller = pjp.getTarget().getClass();
            Authorize authorize_c = ClassUtil.getAnnotation(controller, Authorize.class);
            Authorize authorize_m = ClassUtil.getAnnotation(method, Authorize.class);
            if (authorize_c == null && authorize_m == null) {
                return true;
            }
            config = new AuthorizeConfig();
            initAuthConfig(config, authorize_c);
            initAuthConfig(config, authorize_m);
            configCache.put(cacheName, config);
        }
        if (WebUtil.getLoginUser() == null) {
            throw new BusinessException("");
        }
        return config.doAuth(WebUtil.getLoginUser(), root);
    }

    public String buildCounterKey(String baseKey) {
        return StringUtil.concat(baseKey, "_", DateTimeUtils.format(new Date(), DateTimeUtils.YEAR_MONTH_DAY));
    }

    public Object authorize(ProceedingJoinPoint pjp) {
        ProcessInfo info = new ProcessInfo();
        Object obj = null;
        try {
            boolean access = false;
            String authMsg = authorizationFailsMessage;
            String authCode = "502";
            //尝试验证权限
            try {
                access = doAuth(pjp);
            } catch (BusinessException e) {
                authMsg = "请登陆";
                authCode = "5021";
            }
            if (!access) {
                //未通过授权
                info.setAccess(false);
                obj = new ResponseMessage(false, authMsg, authCode);
                info.setData(obj);
                if (getCounter() != null) {
                    counter.next(buildCounterKey(AuthorizeFailedCounterKey));
                }
            } else {
                //通过授权
                info.setAccess(true);
                info.setInTime(System.currentTimeMillis());
                obj = pjp.proceed();
                info.setData(obj);
                info.setOutTime(System.currentTimeMillis());
                if (getCounter() != null) {
                    counter.next(buildCounterKey(AuthorizeSuccessCounterKey));
                }
            }
        } catch (Throwable e) {
            obj = new ResponseMessage(false, e);
            if (getCounter() != null) {
                counter.next(buildCounterKey(AuthorizeExceptionCounterKey));
            }
        } finally {
            info.setData(obj);
            //尝试输出日志信息
            logger(pjp, info);
        }
        return obj;
    }

    private static final Map<String, String> loggerDescCache = new ConcurrentHashMap<>();

    protected void logger(ProceedingJoinPoint pjp, ProcessInfo info) {
        if (getLoggerService() == null) {
            //已关闭日志服务
            return;
        }
        LogInfo logInfo = new LogInfo();
        //使用request里的attr判断是否命中缓存（并不是很好）
        Object cached = WebUtil.getHttpServletRequest().getAttribute("data_from_cache");
        if (cached != null) {
            logInfo.setCache_key(String.valueOf(cached));
        }
        try {
            //生成日志信息
            Class<?> target = pjp.getTarget().getClass();
            StringBuilder sb = new StringBuilder();
            Method method = ((MethodSignature) pjp.getSignature()).getMethod();
            String methodName = getMethodName(pjp);
            String cacheName = pjp.getTarget().getClass().getName().concat(".").concat(getMethodName(pjp));
            String desc = loggerDescCache.get(cacheName);
            if (desc == null) {
                AccessLogger accessLogger = ClassUtil.getAnnotation(target, AccessLogger.class);
                AccessLogger m_logger = ClassUtil.getAnnotation(method, AccessLogger.class);
                if (accessLogger != null) {
                    sb.append(accessLogger.value());
                    if (m_logger != null) {
                        sb.append("-");
                    }
                }
                if (m_logger != null) {
                    sb.append(m_logger.value());
                }
                loggerDescCache.put(cacheName, desc = sb.toString());
            }

            HttpServletRequest request = WebUtil.getHttpServletRequest();
            Class<?> clazz = pjp.getTarget().getClass();
            logInfo.setU_id(MD5.encode(String.valueOf(System.nanoTime())));
            logInfo.setModule_desc(desc);//方法描述
            logInfo.setClass_name(clazz.getName());//映射类名
            logInfo.setClient_ip(WebUtil.getIpAddr(request));//ip地址
            logInfo.setRequest_method(request.getMethod().concat(".").concat(methodName));//方法：GET.select()
            logInfo.setRequest_header(JSON.toJSONString(WebUtil.getHeaders(request)));//http请求头
            logInfo.setReferer(request.getHeader("referer"));//referer
            logInfo.setRequest_uri(WebUtil.getUri(request, false));//请求相对路径
            logInfo.setRequest_url(WebUtil.getBasePath(request).concat(logInfo.getRequest_uri()));//请求绝对路径
            logInfo.setUser_agent(request.getHeader("User-agent"));//客户端标识
            logInfo.setRequest_param(JSON.toJSONString(WebUtil.getParams(request)));
            User user = WebUtil.getLoginUser();
            if (user != null)
                logInfo.setUser_id(user.getU_id());//当前登录的用户
        } catch (Exception e) {
            logger.error("create logInfo error", e);
            logInfo.setResponse_content(StringUtil.throwable2String(e));
        }
        try {
            logInfo.setRequest_time(info.getInTime());
            Object obj = info.getData();
            if (obj != null) {
                if (obj instanceof ResponseMessage) {
                    ResponseMessage res = (ResponseMessage) obj;
                    if (res.getSourceData() instanceof Throwable) {
                        if (!(res.getSourceData() instanceof BusinessException)) {
                            logInfo.setException_info(StringUtil.throwable2String((Throwable) res.getSourceData()));
                        }
                    }
                    logInfo.setResponse_code(res.getCode());
                } else {
                    logInfo.setResponse_code("200");
                }
                logInfo.setResponse_content(FastJsonHttpMessageConverter.toJson(obj));
            } else {
                logInfo.setResponse_content("null");
            }
            logInfo.setResponse_time(info.getOutTime());
        } catch (Throwable e) {
            logger.error("logger aop proceed error", e);
            logInfo.setException_info(StringUtil.throwable2String(e));
            logInfo.setResponse_code("500");
        }
        //输入日志
        try {
            getLoggerService().log(logInfo);
        } catch (Exception e) {
            logger.error("write access logger error", e);
        }
    }

    public LoggerService getLoggerService() {
        return loggerService;
    }

    public void setLoggerService(LoggerService loggerService) {
        this.loggerService = loggerService;
    }

    public String getAuthorizationFailsMessage() {
        return authorizationFailsMessage;
    }

    public void setAuthorizationFailsMessage(String authorizationFailsMessage) {
        this.authorizationFailsMessage = authorizationFailsMessage;
    }

    public Counter getCounter() {
        return counter;
    }

    public void setCounter(Counter counter) {
        this.counter = counter;
    }

    private static class ProcessInfo {
        private boolean access;

        private Object data;

        private long inTime;

        private long outTime;

        public boolean isAccess() {
            return access;
        }

        public void setAccess(boolean access) {
            this.access = access;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        public long getInTime() {
            return inTime;
        }

        public void setInTime(long inTime) {
            this.inTime = inTime;
        }

        public long getOutTime() {
            return outTime;
        }

        public void setOutTime(long outTime) {
            this.outTime = outTime;
        }

    }

    private static class AuthorizeConfig {

        static class Expression {
            private String id;
            private String type;

            public Expression(String id, String type) {
                this.id = id;
                this.type = type;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }
        }

        Set<String> roles = new HashSet<>();
        Set<String> modules = new HashSet<>();
        Set<String> level = new HashSet<>();
        Set<Expression> expression = new HashSet<>();
        Authorize.MOD mod = Authorize.MOD.INTERSECTION;
        boolean api;

        public boolean doAuth(User user, Map<String, Object> args) {
            boolean success = false;
            if (user == null) return false;
            //优先模块验证
            if (modules.size() != 0) {
                if (level.size() != 0) {
                    m:
                    for (String module : modules) {
                        for (String lv : level) {
                            success = user.hasAccessModuleLevel(module, lv);
                            if (mod == Authorize.MOD.INTERSECTION) {
                                if (success)//只要有一个true就成功
                                    break m;
                            } else {
                                if (!success)//只要有一个false就失败
                                    break m;
                            }
                        }
                    }
                } else {
                    //未设置level
                    for (String module : modules) {
                        success = user.hasAccessModule(module);
                        if (mod == Authorize.MOD.INTERSECTION) {
                            if (success)
                                break;
                        } else {
                            if (!success)
                                break;
                        }
                    }
                }
            } else if (roles.size() > 0) {
                //角色验证
                for (String role : roles) {
                    success = user.hasAccessRole(role);
                    if (mod == Authorize.MOD.INTERSECTION) {
                        if (success)//只要有一个true就成功
                            break;
                    } else {
                        if (!success)//只要有一个false就失败
                            break;
                    }
                }
                //} else if (excludes.size() > 0) {
                //表达式验证 尚未提供支持
                //
            } else if (expression.size() > 0) {
                //验证表达式
                for (Expression expre : expression) {
                    try {
                        //执行表达式
                        args.put("user", user);
                        ExecuteResult result = DynamicScriptEngineFactory.getEngine(expre.getType()).execute(expre.getId(), args);
                        if (result.isSuccess() && "true".equals(String.valueOf(result.getResult()))) {
                            return true;
                        } else {
                            return false;
                        }
                    } catch (Exception e) {
                        return false;
                    }
                }
            } else {
                //只需要登陆即可
                return true;
            }
            return success;
        }

        public Set<String> getRoles() {
            return roles;
        }

        public void setRoles(Set<String> roles) {
            this.roles = roles;
        }

        public Set<String> getModules() {
            return modules;
        }

        public void setModules(Set<String> modules) {
            this.modules = modules;
        }

        public Set<String> getLevel() {
            return level;
        }

        public void setLevel(Set<String> level) {
            this.level = level;
        }

        public void setExpression(Set<Expression> expression) {
            this.expression = expression;
        }

        public Set<Expression> getExpression() {
            return expression;
        }

        public boolean isApi() {
            return api;
        }

        public void setApi(boolean api) {
            this.api = api;
        }

        public Authorize.MOD getMod() {
            return mod;
        }

        public void setMod(Authorize.MOD mod) {
            this.mod = mod;
        }

    }
}
