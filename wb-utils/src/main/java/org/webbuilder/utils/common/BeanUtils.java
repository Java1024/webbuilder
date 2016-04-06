package org.webbuilder.utils.common;

import org.webbuilder.utils.common.bean.AttributeUtil;
import org.webbuilder.utils.common.bean.JDKAttributeUtil;
import org.webbuilder.utils.common.bean.imp.CommonAttributeUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by 浩 on 2015-12-09 0009.
 */
public class BeanUtils {
    private static final AttributeUtil attributeUtil = CommonAttributeUtil.getInstance();

    public static final <T> T attr(String attr, Object value) {
        try {
            return attributeUtil.attr(attr, value);
        } catch (Exception e) {
            return null;
        }
    }

    public static final void attr(Object object, String attr, Object value) throws Exception {
        attributeUtil.attr(object, attr, value);
    }

    private static final Map<Class, List<Method>> getterCache = new ConcurrentHashMap<>();

    private static final Map<Class, List<Method>> setterCache = new ConcurrentHashMap<>();

    public static List<Method> getGetterFromCache(Class clazz) {
        List<Method> methods = getterCache.get(clazz);
        if (methods == null) {
            methods = new ArrayList<>();
            for (Method method : clazz.getDeclaredMethods()) {
                String name = method.getName();
                if (method.getParameterTypes().length == 0) {
                    if (name.startsWith("get")) {
                        methods.add(method);
                    }
                    if (name.startsWith("is")) {
                        methods.add(method);
                    }
                }
            }
            getterCache.put(clazz, methods);
        }
        return methods;
    }

    public static List<Method> getSetterFromCache(Class clazz) {
        List<Method> methods = setterCache.get(clazz);
        if (methods == null) {
            methods = new ArrayList<>();
            for (Method method : clazz.getDeclaredMethods()) {
                String name = method.getName();
                if (name.startsWith("set") && method.getParameterTypes().length == 1 && name.length() > 3) {
                    methods.add(method);
                }
            }
            setterCache.put(clazz, methods);
        }
        return methods;
    }

    public static <T> T copy(Object source, T target) {
        return copy(source, target, false);
    }

    public static <T> T copy(Object source, T target, boolean skipNull) {
        List<Method> getter = getGetterFromCache(source.getClass());
        for (Method method : getter) {
            String methodName = method.getName();
            String attrName;
            byte flag = 3;
            if (methodName.startsWith("is")) {
                flag = 2;
                Field f = JDKAttributeUtil.getFiledFromCache(source.getClass(), methodName);
                if (f != null) {
                    attrName = f.getName();
                } else {
                    attrName = String.valueOf(methodName.charAt(flag)).toLowerCase().concat(methodName.substring(flag + 1));
                }
            } else {
                attrName = String.valueOf(methodName.charAt(flag)).toLowerCase().concat(methodName.substring(flag + 1));
            }
            try {
                Object data = method.invoke(source);
                if (data == null && skipNull) continue;
                attr(target, attrName, data);
            } catch (Exception e) {
            }
        }
        return target;
    }

}
