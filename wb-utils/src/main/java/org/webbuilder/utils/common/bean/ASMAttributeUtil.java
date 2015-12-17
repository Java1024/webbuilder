package org.webbuilder.utils.common.bean;

import com.esotericsoftware.reflectasm.MethodAccess;
import org.webbuilder.utils.common.StringUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 浩 on 2015-12-09 0009.
 */
public class ASMAttributeUtil extends JDKAttributeUtil {

    //asm方法缓存
    protected static final Map<Class, MethodAccess> METHOD_ACCESS_CACHE = new ConcurrentHashMap<>();

    @Override
    protected <T> T attrSimple(String attr, Object value) throws Exception {
        //如果为map 直接get
        if (value instanceof Map) {
            return (T) ((Map) value).get(attr);
        }
        Class type = value.getClass();
        MethodAccess access = METHOD_ACCESS_CACHE.get(type);
        if (access == null) {
            access = MethodAccess.get(type);
            METHOD_ACCESS_CACHE.put(type, access);
        }
        //属性
        Field field = getFiledFromCache(type, attr);
        //getter方法
        Class fType = field != null ? field.getType() : Object.class;
        String getter = ((fType == boolean.class || fType == Boolean.class) ? "is" : "get").concat(StringUtils.toUpperCaseFirstOne(attr));
        return (T) access.invoke(value, getter);
    }

    @Override
    protected void attrSimple(Object object, String attr, Object value) throws Exception {
        //如果为map 直接get
        if (object instanceof Map) {
            ((Map) object).put(attr, value);
        }
        Class type = object.getClass();
        MethodAccess access = METHOD_ACCESS_CACHE.get(type);
        if (access == null) {
            access = MethodAccess.get(type);
            METHOD_ACCESS_CACHE.put(type, access);
        }
        //getter方法
        String getter = "set".concat(StringUtils.toUpperCaseFirstOne(attr));
        access.invoke(object, getter, value);
    }
}
