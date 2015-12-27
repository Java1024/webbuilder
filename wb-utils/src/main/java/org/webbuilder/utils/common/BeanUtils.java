package org.webbuilder.utils.common;

import org.webbuilder.utils.common.bean.AttributeUtil;
import org.webbuilder.utils.common.bean.JDKAttributeUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by 浩 on 2015-12-09 0009.
 */
public class BeanUtils {

    private static final AttributeUtil jdkAttributeUtil = new JDKAttributeUtil();


    public static final <T> T attr(String attr, Object value) {
        try {
            return jdkAttributeUtil.attr(attr, value);
        } catch (Exception e) {
            return null;
        }
    }

    public static final void attr(Object object, String attr, Object value) throws Exception {
        jdkAttributeUtil.attr(object, attr, value);
    }


    private static final Map<Class, List<Method>> getterCache = new ConcurrentHashMap<>();

    private static final Map<Class, List<Method>> setterCache = new ConcurrentHashMap<>();

    private static List<Method> getGetterFromCache(Class clazz) {
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

    private static List<Method> getSetterFromCache(Class clazz) {
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
            }
            attrName = String.valueOf(methodName.charAt(flag)).toLowerCase().concat(methodName.substring(flag + 1));
            try {
                Object data = method.invoke(source);
                if (data == null && skipNull) continue;
                attr(target, attrName, data);
            } catch (Exception e) {
            }
        }
        return target;
    }

    public static void main(String[] args) {
        String methodName = "getName";
        String method = String.valueOf(methodName.charAt(3)).toLowerCase().concat(methodName.substring(4));
        System.out.println(method);
    }

}
