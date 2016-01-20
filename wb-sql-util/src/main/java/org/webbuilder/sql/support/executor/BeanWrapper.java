package org.webbuilder.sql.support.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webbuilder.utils.common.BeanUtils;
import org.webbuilder.utils.common.ClassUtils;

/**
 * Created by 浩 on 2015-12-21 0021.
 */
public abstract class BeanWrapper<T> implements ObjectWrapper<T> {

    private Class<T> type;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public T newInstance() {
        try {
            return getType().newInstance();
        } catch (Exception e) {
            logger.error("new instance error!", e);
        }
        return null;
    }

    @Override
    public void wrapper(T instance, int index, String attr, Object value) {
        try {
            BeanUtils.attr(instance, attr, value);
        } catch (Exception e) {
            logger.error("set attr error", e);
        }
    }

    @Override
    public void done(T instance) {

    }

    public Class<T> getType() {
        if (type == null) type = (Class<T>) ClassUtils.getGenericType(this.getClass());
        return type;
    }

    public void setType(Class<T> type) {
        this.type = type;
    }
}
