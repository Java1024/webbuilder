package org.webbuilder.utils.common.bean.imp;

import org.webbuilder.utils.common.ClassUtils;
import org.webbuilder.utils.common.bean.BeanCopyUtil;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 浩 on 2016-01-12 0012.
 */
public class CommonBeanCopyUtil implements BeanCopyUtil {

    protected CommonAttributeUtil attributeUtil = CommonAttributeUtil.getInstance();

    @Override
    public <T> T copy(Object source, T target) throws Exception {
        if (source instanceof Map) {
            if (target instanceof Map) {
                ((Map) target).putAll(((Map) source));
            } else {
                CommonAttributeUtil.CacheInfo cacheInfo = attributeUtil.getPropertyFromCache(target.getClass());
                for (PropertyDescriptor descriptor : cacheInfo.getPropertyDescriptors()) {
                    Object val = ((Map) target).get(descriptor.getName());

                }
            }
        }
        CommonAttributeUtil.CacheInfo cacheInfo = attributeUtil.getPropertyFromCache(source.getClass());
        if (target instanceof Map) {
            Map<String, Object> target_map = (Map) target;
            for (Map.Entry<String, Method> entry : cacheInfo.getGetter().entrySet()) {
                target_map.put(entry.getKey(), entry.getValue().invoke(source));
            }
        } else {

        }
        return target;
    }

    @Override
    public <T> T copy(Object source, T target, boolean skipNull) throws Exception {
        if (source instanceof Map) {
            if (target instanceof Map) {
                ((Map) target).putAll(((Map) source));
            } else {
                for (Map.Entry<String, Object> entry : ((Map<String, Object>) source).entrySet()) {
                    if (entry.getValue() == null && skipNull) continue;
                    attributeUtil.attr(target, entry.getKey(), entry.getValue());
                }
            }
            return target;
        }
        CommonAttributeUtil.CacheInfo cacheInfo = attributeUtil.getPropertyFromCache(source.getClass());
        if (target instanceof Map) {
            Map<String, Object> target_map = (Map) target;
            for (Map.Entry<String, Method> entry : cacheInfo.getGetter().entrySet()) {
                target_map.put(entry.getKey(), entry.getValue().invoke(source));
            }
        } else {

        }
        return target;
    }

    @Override
    public <T> T deepCopy(Object source, T target) throws Exception {
        return null;
    }

    @Override
    public <T> T deepCopy(Object source, T target, boolean skipNull) throws Exception {
        return null;
    }

    protected <T> T copy(Object source, T target, boolean deep, boolean skipNull) throws Exception {
        if (source instanceof Map) {
            if (target instanceof Map) {
                ((Map) target).putAll(((Map) source));
            } else {
                for (Map.Entry<String, Object> entry : ((Map<String, Object>) source).entrySet()) {
                    Object value = entry.getValue();
                    if (value != null) {
                        if (deep && !ClassUtils.isCommonClass(value.getClass())) {
                            Object newValue = value.getClass().newInstance();
                            value = copy(value, newValue, deep, skipNull);
                        }
                        attributeUtil.attr(target, entry.getKey(), value);
                    } else if (skipNull) continue;

                }
            }
            return target;
        }
        CommonAttributeUtil.CacheInfo cacheInfo = attributeUtil.getPropertyFromCache(source.getClass());
        if (target instanceof Map) {
            Map<String, Object> target_map = (Map) target;
            for (Map.Entry<String, Method> entry : cacheInfo.getGetter().entrySet()) {
                target_map.put(entry.getKey(), entry.getValue().invoke(source));
            }
        } else {

        }
        return target;
    }
}
