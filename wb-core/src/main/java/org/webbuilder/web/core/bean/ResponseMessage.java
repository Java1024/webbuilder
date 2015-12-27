package org.webbuilder.web.core.bean;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webbuilder.utils.common.ClassUtils;
import org.webbuilder.utils.common.DateTimeUtils;
import org.webbuilder.web.core.exception.AccessValidException;
import org.webbuilder.web.core.exception.BusinessException;

import javax.validation.ValidationException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 响应消息。controller中处理后，返回此对象，响应请求结果给客户端。
 */
public class ResponseMessage {

    /**
     * message处理类，可以自定义message处理方案
     */
    private transient static final Map<Class, MessageHandler> handlers = new ConcurrentHashMap<>();

    /**
     * jsoup跨域请求
     */
    private transient String callback;

    static {
        handlers.put(Object.class, new MessageHandler<Object>() {
            @Override
            public Object handle(ResponseMessage message, Object msg) {
                return msg;
            }
        });
        //默认异常信息处理
        handlers.put(Exception.class, new MessageHandler<Exception>() {
            @Override
            public Object handle(ResponseMessage message,Exception e) {
                LOGGER.error(e.getMessage(), e);
                return e.getMessage();
            }
        });
        //默认业务异常信息处理
        handlers.put(BusinessException.class, new MessageHandler<BusinessException>() {
            @Override
            public Object handle(ResponseMessage message,BusinessException e) {
                LOGGER.error(e.getMessage());
                return e.getMessage();
            }
        });
        //验证器异常，不记录错误日志
        handlers.put(ValidationException.class, new MessageHandler<BusinessException>() {
            @Override
            public Object handle(ResponseMessage message,BusinessException e) {
                message.setCode("400");
                return e.getMessage();
            }
        });
    }

    private transient static final Logger LOGGER = LoggerFactory.getLogger(ResponseMessage.class);

    /**
     * 响应格式
     */
    public transient static final String CONTENT_TYPE_JSON = "application/json;charset=UTF-8";

    /**
     * 响应格式
     */
    public transient static final String CONTENT_TYPE_HTML = "text/html;charset=UTF-8";

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 反馈数据
     */
    private Object data;

    /**
     * 响应码
     */
    private String code;

    /**
     * 进行响应的元数据,不会被序列化,只是提供aop和拦截器访问
     */
    private transient Object sourceData;


    public ResponseMessage(boolean success, Object data) {
        this.code = success ? "200" : "500";
        if (data == null)
            data = "null";
        sourceData = data;
        //获取消息处理器
        MessageHandler messageHandler = getMessageHandler(data.getClass());
        if (messageHandler == null) {
            if (data instanceof Throwable) {
                //未获取到指定的异常信息处理器，使用通用异常处理器
                messageHandler = getMessageHandler(Exception.class);
                if (data instanceof ValidationException) {
                    this.code = "400";
                } else if (data instanceof AccessValidException) {
                    this.code = "502";
                }
            } else {
                messageHandler = getMessageHandler(Object.class);
            }
        }
        this.success = success;
        if (messageHandler == null)
            this.data = data;
        else
            this.data = messageHandler.handle(this, data);

    }

    public ResponseMessage(boolean success, Object data, String code) {
        this(success, data);
        this.code = code;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Object getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return JSON.toJSONStringWithDateFormat(this, DateTimeUtils.YEAR_MONTH_DAY_HOUR_MINUTE_SECOND);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public <T> void registerMessageHandler(MessageHandler<T> handler) {
        Class type = ClassUtils.getGenericType(handler.getClass());
        handlers.put(type, handler);
    }

    private <T> MessageHandler<T> getMessageHandler(Class<T> type) {
        return handlers.get(type);
    }

    public void removeMessageHandler(Class type) {
        handlers.remove(type);
    }

    public interface MessageHandler<T> {
        Object handle(ResponseMessage message, T msg);
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public ResponseMessage callback(String callback) {
        this.callback = callback;
        return this;
    }

    public Object getSourceData() {
        return sourceData;
    }

    public static ResponseMessage fromJson(String json) {
        return JSON.parseObject(json, ResponseMessage.class);
    }
}
