package org.webbuilder.utils.common.bean;

import org.webbuilder.utils.common.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 浩 on 2015-12-09 0009.
 */
public class JDKAttributeUtil implements AttributeUtil {

    //方法缓存
    protected static final Map<String, Method> method_cache = new ConcurrentHashMap<>();

    //字段缓存
    protected static final Map<String, Field> field_cache = new ConcurrentHashMap<>();

    /**
     * 反射获取对象中属性值,
     * 获取逻辑:获取所有指定属性的 get方法.(field比一定存在)
     * 如果是boolean类型,调用is方法.否则调用get方法
     *
     * @param value 获取对象
     * @param attr  属性名
     * @param <T>   返回类型泛型
     * @return 获取到的结果，未获取到返回null
     * @throws Exception 获取异常，如果get方法不存在，可能抛出异常
     */
    protected <T> T attrSimple(String attr, Object value) throws Exception {
        //如果为map 直接get
        if (value instanceof Map) {
            return (T) ((Map) value).get(attr);
        }
        Class type = value.getClass();
        //属性
        Field field = getFiledFromCache(type, attr);
        //getter方法
        Class fType = field != null ? field.getType() : Object.class;
        String getter = ((fType == boolean.class || fType == Boolean.class) ? "is" : "get").concat(StringUtils.toUpperCaseFirstOne(attr));
        Method getMethod = geMethodFromCache(type, getter);
        if (getMethod == null) {
            return null;
        }
        //调用getter方法
        return (T) getMethod.invoke(value);
    }

    /**
     * 尝试从缓存里获取一个类型的字段属性,如果未命中缓存,则通过反射获取后放入缓存
     *
     * @param type 目标类型
     * @param attr 目标字段属性
     * @return 字段属性对象, 如果不存在返回null
     */
    public static final  Field getFiledFromCache(Class type, String attr) {
        //属性
        String f_cacheName = StringUtils.concat(type.getName(), ".", attr);
        Field field = field_cache.get(f_cacheName);
        if (field == null) {
            try {
                field = type.getDeclaredField(attr);
                field_cache.put(f_cacheName, field);
            } catch (Exception e) {
            }
        }
        return field;
    }

    /**
     * 向对象中设置一个属性值,通过调用set方法进行设置,如果属性不存在,也将不进行设置
     *
     * @param object 目标对象
     * @param attr   属性
     * @param value  值
     * @throws Exception 设置失败异常
     */
    protected void attrSimple(Object object, String attr, Object value) throws Exception {
        //如果为map 直接put
        if (object instanceof Map) {
            ((Map) object).put(attr, value);
        }
        Class type = object.getClass();
        Class fType;
        //属性
        Field field = getFiledFromCache(type, attr);
        if (field == null) {
            return;
        }
        fType = field.getType();
        String setter = "set".concat(StringUtils.toUpperCaseFirstOne(attr));
        Method method = geMethodFromCache(type, setter, fType);
        if (method == null) return;
        method.invoke(object, value);
    }

    /**
     * 尝试从缓存里获取一个类型的方法,如果未命中缓存,则通过反射获取后放入缓存
     *
     * @param type       目标类型
     * @param methodName 目标字段属性
     * @param paramType  参数类型列表
     * @return 方法, 如果不存在返回null
     */
    public static final  Method geMethodFromCache(Class type, String methodName, Class... paramType) {
        //属性
        String m_cacheName = StringUtils.concat(type.getName(), ".", methodName, paramType);
        Method method = method_cache.get(m_cacheName);
        if (method == null) {
            try {
                method = type.getDeclaredMethod(methodName, paramType);
                method_cache.put(m_cacheName, method);
            } catch (Exception e) {
            }
        }
        return method;
    }

    /**
     * 向指定的对象填充一个属性值,属性名支持嵌套写法。如:userInfo.name
     * 当属性名为嵌套写法时,属性的类型必须为非接口或抽象类型并且有无参构造方法。
     *
     * @param object 要进行填充属性值的对象
     * @param attr   属性名
     * @param value  值
     * @throws Exception
     */
    @Override
    public void attr(Object object, String attr, Object value) throws Exception {
        if (value == null) return;
        if (!attr.contains(".")) {
            //不为嵌套属性者直接设置值
            attrSimple(object, attr, value);
        } else {
            String[] newF = StringUtils.splitFirst(attr, "[.]");
            String filedName = newF[0];//属性名
            //尝试获取已经存在的值
            Object attInstance = attrSimple(filedName, object);
            if (attInstance == null) {
                //如果原始对象为map,则创建map
                if (object instanceof Map) {
                    attInstance = newInstance(Map.class);
                } else {
                    Field fieldObj = getFiledFromCache(object.getClass(), filedName);
                    if (fieldObj == null) return;
                    Class type = fieldObj.getType();
                    if (attInstance == null && !type.isInterface()) {
                        try {
                            attInstance = newInstance(type);
                        } catch (Exception e) {
                        }
                    }
                }
                if (attInstance != null)
                    attrSimple(object, filedName, attInstance);
            }
            if (attInstance != null) {

                attr(attInstance, newF[1], value);
            }
        }
    }

    @Override
    public <T> T attr(String attr, Object object) throws Exception {
        if (object == null) return null;
        if (!attr.contains(".")) {
            return this.attrSimple(attr, object);
        } else {
            String[] newF = StringUtils.splitFirst(attr, "[.]");
            Object newVal = this.attrSimple(newF[0], object);
            return attr(newF[1], newVal);
        }
    }

    protected <T> T newInstance(Class<T> tClass) {
        if (tClass == Map.class) {
            return (T) new LinkedHashMap<>();
        } else if (tClass == List.class) {
            return (T) new ArrayList<>();
        }
        try {
            return tClass.newInstance();
        } catch (Exception e) {

        }
        return null;
    }
}
