package org.webbuilder.utils.common.bean;

/**
 * Created by 浩 on 2015-12-30 0030.
 */
public interface BeanCopyUtil {
    <T> T copy(Object source, T target) throws Exception;

    <T> T copy(Object source, T target, boolean skipNull) throws Exception;

    <T> T deepCopy(Object source, T target) throws Exception;

    <T> T deepCopy(Object source, T target, boolean skipNull) throws Exception;

}
