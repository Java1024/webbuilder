package org.webbuilder.sql.support.executor;

import org.webbuilder.utils.common.StringUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于HashMap的包装器,用于将查询结果包装为HashMap
 * Created by 浩 on 2015-11-10 0010.
 */
public class HashMapWrapper implements ObjectWrapper<Map<String, Object>> {

    private static final Map<Class, DataFormat> formatBase = new ConcurrentHashMap<>();

    static {
        //提供将BigDecimal类型转为Long类型
        addFormat(new DataFormat<BigDecimal, Long>() {
            @Override
            public Class<BigDecimal> support() {
                return BigDecimal.class;
            }

            @Override
            public Long format(BigDecimal data) {
                return data.longValue();
            }
        });
        //提供将Timestamp类型转为Date类型
        addFormat(new DataFormat<Timestamp, Date>() {
            @Override
            public Class<Timestamp> support() {
                return Timestamp.class;
            }

            @Override
            public Date format(Timestamp data) {
                return new Date(data.getTime());
            }
        });
    }

    public HashMapWrapper() {
    }

    public static <T extends DataFormat> T addFormat(T format) {
        formatBase.put(format.support(), format);
        return format;
    }

    @Override
    public Map<String, Object> newInstance() {
        return new LinkedHashMap<>();
    }

    @Override
    public void wrapper(Map<String, Object> instance, int index, String attr, Object value) {
        if ("ROWNUM_".equals(attr)) return;
        if (value != null) {
            Class clazz = value.getClass();
            DataFormat format = formatBase.get(clazz);
            if (format != null)
                value = format.format(value);
        }
        putValue(instance, attr, value);
    }

    /**
     * 向一个map实例填充一个属性,属性支持嵌套如,userInfo.name
     *
     * @param instance 要进行填充的实例
     * @param attr     属性名
     * @param value    属性值
     */
    public void putValue(Map<String, Object> instance, String attr, Object value) {
        if (attr.contains(".")) {
            String[] attrs = StringUtils.splitFirst(attr, "[.]");
            String attr_ob_name = attrs[0];
            String attr_ob_attr = attrs[1];
            Object object = instance.get(attr_ob_name);
            if (object == null) {
                object = newInstance();
                instance.put(attr_ob_name, object);
            }
            if (object instanceof Map) {
                Map<String, Object> objectMap = (Map) object;
                putValue(objectMap, attr_ob_attr, value);
            }
        } else {
            instance.put(attr, value);
        }
    }

    @Override
    public void done(Map<String, Object> instance) {

    }

}
