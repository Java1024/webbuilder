package org.webbuilder.sql.support.executor;

/**
 * 数据格式化接口,用于进行数据转换
 * Created by 浩 on 2015-11-10 0010.
 */
public interface DataFormat<T, V> {
    /**
     * 提供转换的类型
     *
     * @return 类型
     */
    Class<T> support();

    /**
     * 进行数据转换
     *
     * @param data 转换前的数据
     * @return 转换后的数据
     */
    V format(T data);
}
