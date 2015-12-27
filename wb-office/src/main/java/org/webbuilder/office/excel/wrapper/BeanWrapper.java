package org.webbuilder.office.excel.wrapper;

import org.webbuilder.utils.common.ClassUtils;
import org.webbuilder.utils.common.StringUtils;
import org.webbuilder.utils.common.BeanUtils;
import org.webbuilder.utils.common.bean.JDKAttributeUtil;

import java.lang.reflect.Field;

/**
 * Created by 浩 on 2015-12-07 0007.
 */
public class BeanWrapper<T> extends AbstractWrapper<T> {

    private Class<T> type;

    @Override
    public T newInstance() throws Exception {
        if (type == null) {
            type = (Class<T>) ClassUtils.getGenericType(this.getClass());
        }
        return type.newInstance();
    }

    @Override
    public void wrapper(T instance, String header, Object value) {
        if (header == null || "".equals(header)) return;
        header = headerMapper(header);
        try {
            Field field = JDKAttributeUtil.getFiledFromCache(type, header);
            if (field != null) {
                value = changeType(value, field.getType());
            }
            BeanUtils.attr(instance, header, value);
        } catch (Exception e) {
        }
    }


    protected Object changeType(Object value, Class<?> paramType) {
        if (value.getClass() == paramType) return value;
        if (paramType == int.class || paramType == Integer.class) {
            value = StringUtils.toInt(value);
        }
        if (paramType == double.class || paramType == Double.class) {
            value = StringUtils.toDouble(value);
        }
        if (paramType == float.class || paramType == Float.class) {
            value = (float) StringUtils.toDouble(value);
        }
        return value;
    }

    public void setType(Class<T> type) {
        this.type = type;
    }
}
