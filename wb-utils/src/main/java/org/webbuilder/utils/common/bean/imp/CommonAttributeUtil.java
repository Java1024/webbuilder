package org.webbuilder.utils.common.bean.imp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webbuilder.utils.common.ClassUtils;
import org.webbuilder.utils.common.DateTimeUtils;
import org.webbuilder.utils.common.StringUtils;
import org.webbuilder.utils.common.bean.AttributeUtil;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 浩 on 2016-01-12 0012.
 */
public class CommonAttributeUtil implements AttributeUtil {

    private static final Map<Class, CacheInfo> cache = new ConcurrentHashMap<>();

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final CommonAttributeUtil instance = new CommonAttributeUtil();

    public static final CommonAttributeUtil getInstance() {
        return instance;
    }

    private CommonAttributeUtil() {
    }

    public CacheInfo getPropertyFromCache(Class clazz) {
        CacheInfo cached = cache.get(clazz);
        List<PropertyDescriptor> descriptors = new ArrayList<>();
        Map<String, Method> getter = new HashMap<>();
        Map<String, Method> setter = new HashMap<>();
        if (cached == null) {
            cached = new CacheInfo();
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
                PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
                for (PropertyDescriptor property : propertyDescriptors) {
                    String key = property.getName();
                    // 过滤class属性
                    if (!key.equals("class")) {
                        descriptors.add(property);
                        getter.put(key, property.getReadMethod());
                        setter.put(key, property.getWriteMethod());
                    }
                }
            } catch (Exception e) {
                logger.error("获取beanInfo失败", e);
            }
            cached.setGetter(getter);
            cached.setSetter(setter);
            cached.setPropertyDescriptors(descriptors);
            cache.put(clazz, cached);
        }
        return cached;
    }

    @Override
    public void attr(Object object, String attr, Object value) throws Exception {
        if (object instanceof Map) {
            ((Map) object).put(attr, value);
            return;
        }
        CacheInfo cache = getPropertyFromCache(object.getClass());
        Method method = cache.getSetter().get(attr);
        if (method != null) {
            method.invoke(object, ClassUtils.cast(value, method.getParameterTypes()[0]));
        }
    }

    @Override
    public <T> T attr(String attr, Object object) throws Exception {
        if (object instanceof Map) {
            return (T) ((Map) object).get(attr);
        }
        CacheInfo cache = getPropertyFromCache(object.getClass());
        Method method = cache.getGetter().get(attr);
        if (method != null) {
            return (T) method.invoke(object);
        }
        return null;
    }

    public static class CacheInfo {
        List<PropertyDescriptor> propertyDescriptors;
        Map<String, Method> getter;
        Map<String, Method> setter;

        public List<PropertyDescriptor> getPropertyDescriptors() {
            return propertyDescriptors;
        }

        public void setPropertyDescriptors(List<PropertyDescriptor> propertyDescriptors) {
            this.propertyDescriptors = propertyDescriptors;
        }

        public Map<String, Method> getGetter() {
            return getter;
        }

        public void setGetter(Map<String, Method> getter) {
            this.getter = getter;
        }

        public Map<String, Method> getSetter() {
            return setter;
        }

        public void setSetter(Map<String, Method> setter) {
            this.setter = setter;
        }
    }

}
