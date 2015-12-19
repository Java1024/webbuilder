package org.webbuilder.utils.common;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by 浩 on 2015-12-09 0009.
 */
public class ClassUtils {

    /**
     * 获取一个类的注解,如果未获取到则获取父类
     *
     * @param clazz      要获取的类
     * @param annotation 注解类型
     * @param <T>        注解类型泛型
     * @return 注解
     */
    public static <T extends Annotation> T getAnnotation(Class<?> clazz, Class<T> annotation) {
        T ann = clazz.getAnnotation(annotation);
        if (ann != null) {
            return ann;
        } else {
            if (clazz.getSuperclass() != Object.class) {
                //尝试获取父类
                return getAnnotation(clazz.getSuperclass(), annotation);
            }
        }
        return ann;
    }

    /**
     * 获取一个方法的注解,如果未获取则获取父类方法
     *
     * @param method     要获取的方法
     * @param annotation 注解类型
     * @param <T>        注解类型泛型
     * @return
     */
    public static <T extends Annotation> T getAnnotation(Method method, Class<T> annotation) {
        T ann = method.getAnnotation(annotation);
        if (ann != null) {
            return ann;
        } else {
            Class clazz = method.getDeclaringClass();
            Class superClass = clazz.getSuperclass();
            if (superClass != Object.class) {
                try {
                    //父类方法
                    Method suMethod = superClass.getMethod(method.getName(), method.getParameterTypes());
                    return getAnnotation(suMethod, annotation);
                } catch (NoSuchMethodException e) {
                    return null;
                }
            }
        }
        return ann;
    }

    /**
     * 获取一个类的泛型类型,如果未获取到返回Object.class
     *
     * @param clazz 要获取的类
     * @param index 泛型索引
     * @return 泛型
     */
    public static Class<?> getGenericType(Class clazz, int index) {
        Type genType = clazz.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (index >= params.length || index < 0) {
            throw new RuntimeException("Index outof bounds");
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }
        return (Class) params[index];
    }

    /**
     * 获取一个类的第一个泛型的类型
     *
     * @param clazz 要获取的类
     * @return 泛型
     */
    public static Class<?> getGenericType(Class clazz) {
        return getGenericType(clazz, 0);
    }


    public static boolean instanceOf(Class clazz, Class target) {
        if (clazz == null) return false;
        if (clazz == target) return true;
        if (target.isInterface()) {
            for (Class aClass : clazz.getInterfaces()) {
                if (aClass == target) return true;
            }
        }
        if (clazz.getSuperclass() == target) return true;
        else {
            if (clazz.isInterface()) {
                for (Class aClass : clazz.getInterfaces()) {
                    if (instanceOf(aClass, target)) return true;
                }
            }
            return instanceOf(clazz.getSuperclass(), target);
        }
    }

    public static final Set<Class> commonClass = new HashSet<>();

    static {
        commonClass.add(int.class);
        commonClass.add(double.class);
        commonClass.add(float.class);
        commonClass.add(byte.class);
        commonClass.add(short.class);
        commonClass.add(char.class);
    }

    public static boolean isCommonClass(Class clazz) {
        return commonClass.contains(clazz);
    }

}
