package org.webbuilder.web.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webbuilder.utils.base.StringUtil;
import org.webbuilder.web.core.bean.JsonParam;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * json参数 转换器，用于将json格式的参数转换为对象
 * Created by 浩 on 2015-09-29 0029.
 */
public class JsonParamMethodArgumentResolver implements HandlerMethodArgumentResolver {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        //注解了JsonParam则支持此功能
        return methodParameter.hasParameterAnnotation(JsonParam.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter,
                                  ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest,
                                  WebDataBinderFactory webDataBinderFactory) throws Exception {
        JsonParam jsonParam = methodParameter.getParameterAnnotation(JsonParam.class);
        Class<?> type = jsonParam.type();
        if (type == Object.class) {
            type = methodParameter.getParameterType();
        }
        String json;
        if (!"".equals(jsonParam.value())) {
            json = nativeWebRequest.getParameter(jsonParam.value());
            if (json == null)
                json = jsonParam.defaultValue();
        } else {
            //将参数列表也识别为json
            Map<String, String[]> map = nativeWebRequest.getParameterMap();
            JSONObject jsonObject = new JSONObject();
            for (Map.Entry<String, String[]> entry : map.entrySet()) {
                if (entry.getValue().length > 0) {
                    String val = entry.getValue()[0];
                    Object obj = val;
                    try {
                        Field field = methodParameter.getParameterType().getDeclaredField(entry.getKey());
                        if (field.getType().isAssignableFrom(Number.class) || Arrays.asList("int", "double", "long").contains(field.getType().getSimpleName())) {
                            if (!StringUtil.isNumber(obj)) {
                                continue;
                            }
                        }
                    } catch (Exception e) {
                        continue;
                    }
                    if ((val.startsWith("{") && val.endsWith("}"))) {
                        try {
                            obj = JSON.parseObject(val, Map.class);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if ((val.startsWith("[") && val.endsWith("]"))) {
                        try {
                            obj = JSON.parseObject(val, List.class);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {

                    }
                    jsonObject.put(entry.getKey(), obj);
                }
            }
            json = jsonObject.toJSONString();
        }
        try {
            return JSON.parseObject(json, type);
        } catch (Exception e) {
            logger.error("init JsonParam error", e);
            return type.newInstance();
        }
    }

}
