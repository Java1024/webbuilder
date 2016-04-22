package org.webbuilder.sql;

import com.alibaba.fastjson.JSON;
import org.webbuilder.utils.common.ClassUtils;
import org.webbuilder.utils.common.DateTimeUtils;
import org.webbuilder.utils.common.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zhouhao on 16-4-14.
 */
public class SimpleValueWrapper implements ValueWrapper {

    private Object value;

    public SimpleValueWrapper(Object value) {
        this.value = value;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public int toInt() {
        return Integer.parseInt(toString());
    }

    @Override
    public double toDouble() {
        return Double.parseDouble(toString());
    }

    @Override
    public boolean toBoolean() {
        return "true".equals(toString().toLowerCase());
    }

    @Override
    public double toDouble(double defaultValue) {
        return StringUtils.toDouble(value, defaultValue);
    }

    @Override
    public int toInt(int defaultValue) {
        return StringUtils.toInt(value, defaultValue);
    }

    @Override
    public boolean toBoolean(boolean defaultValue) {
        if (StringUtils.isNullOrEmpty(value)) return defaultValue;
        return toBoolean();
    }

    @Override
    public Date toDate() {
        return DateTimeUtils.formatUnknownString2Date(toString());
    }

    @Override
    public Date toDate(String format) {
        return DateTimeUtils.formatDateString(toString(), format);
    }

    @Override
    public <T> T toBean(Class<T> type) {
        if (valueTypeOf(type)) return ((T) getValue());
        return JSON.parseObject(toString(), type);
    }

    @Override
    public <T> List<T> toBeanList(Class<T> type) {
        if (getValue() instanceof List) return ((List) getValue());
        return JSON.parseArray(toString(), type);
    }

    @Override
    public boolean valueTypeOf(Class<?> type) {
        if (value == null) return false;
        return ClassUtils.instanceOf(value.getClass(), type);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public String toString(String defaultValue) {
        if (value == null) return defaultValue;
        return toString();
    }

    @Override
    public List<Map> toList() {
        return toBeanList(Map.class);
    }

    @Override
    public Map<String, Object> toMap() {
        return toBean(Map.class);
    }
}


