package org.webbuilder.sql;

import java.io.Serializable;

/**
 * 数据库接口，此接口对外通过获取表信息，创建表和修改表操作
 * Created by 浩 on 2015-11-06 0006.
 */
public interface DataBase extends Serializable {

    /**
     * 获取数据库元数据
     *
     * @return 数据库元数据
     */
    DataBaseMetaData getMetaData();

    /**
     * 获取数据库表访问接口
     *
     * @param name 表名
     * @return 数据库表访问接口
     */
    Table getTable(String name);

    /**
     * 根据数据库表元数据创建数据库表
     *
     * @param tableMetaData 表元数据
     * @return 创建后的数据库表访问接口
     * @throws Exception 创建异常信息
     */
    Table createTable(TableMetaData tableMetaData) throws Exception;

    /**
     * 根据数据库表元数据修改数据库表
     *
     * @param tableMetaData 表元数据
     * @return 修改后的数据库表访问接口
     * @throws Exception 修改异常信息
     */
    Table alterTable(TableMetaData tableMetaData) throws Exception;

    /**
     * 只更新表结构，不对数据库进行修改
     *
     * @param tableMetaData 表元数据
     * @return 修改后的数据库表访问接口
     * @throws Exception 修改异常信息
     */
    Table updateTable(TableMetaData tableMetaData) throws Exception;

    void removeTable(String name) throws Exception;
}
