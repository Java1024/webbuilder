package org.webbuilder.sql.support.executor;

import org.webbuilder.sql.DataBase;
import org.webbuilder.sql.Table;

/**
 * 对象包装器工厂，dataBase接口通过此工厂来获取对象包装器。
 * Created by 浩 on 2016-01-26 0026.
 */
public interface ObjectWrapperFactory<T> {

    /**
     * 获取一个对象包装器
     *
     * @param dataBase 数据库实例
     * @param table    表操作实例
     * @return 对象包装器
     */
    ObjectWrapper<T> getWrapper(DataBase dataBase, Table table);
}
