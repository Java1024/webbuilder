package org.webbuilder.office.excel.wrapper;

import org.webbuilder.utils.base.ClassUtil;
import org.webbuilder.utils.base.StringUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 浩 on 2015-12-07 0007.
 */
public class BeanWrapper<T> extends AbstractWrapper<T> {

    private Class<T> type;

    private Map<String, Method> methodCache = new HashMap<>();

    @Override
    public T newInstance() throws Exception {
        if (type == null) {
            type = (Class<T>) ClassUtil.getGenericType(this.getClass());
        }
        return type.newInstance();
    }

    @Override
    public void wrapper(T instance, String header, Object value) {
        if (header == null || "".equals(header)) return;
        header = headerMapper(header);
        setValue(instance, header, value);
    }

    protected Method getMethodByName(String field) throws Exception {
        String setterName = "set".concat(StringUtil.toUpperCaseFirstOne(field));
        Method method1 = methodCache.get(type.getName().concat(setterName));
        if (method1 == null) {
            try {
                Field field1 = type.getDeclaredField(field);
                method1 = type.getMethod(setterName, field1.getType());
                methodCache.put(type.getName().concat(setterName), method1);
            } catch (Exception e) {
                return null;
            }
        }
        return method1;
    }

    protected void setValue(T instance, String field, Object value) {
        try {
            Method method = getMethodByName(field);
            if (method != null) {
                Class<?> paramType = method.getParameterTypes()[0];
                value = changeType(value, paramType);
                method.invoke(instance, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected Object changeType(Object value, Class<?> paramType) {
        if (value.getClass() == paramType) return value;
        if(paramType ==int.class||paramType==Integer.class){
            value=StringUtil.toInt(value);
        } if(paramType ==double.class||paramType==Double.class){
            value=StringUtil.toDouble(value);
        }if(paramType ==float.class||paramType==Float.class){
            value=(float)StringUtil.toDouble(value);
        }
        return value;
    }

    public void setType(Class<T> type) {
        this.type = type;
    }
}
