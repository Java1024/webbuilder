package org.webbuilder.sql;

import org.webbuilder.sql.exception.DeleteException;
import org.webbuilder.sql.exception.InsertException;
import org.webbuilder.sql.exception.QueryException;
import org.webbuilder.sql.exception.UpdateException;

import java.io.Serializable;

/**
 * 数据库表访问接口
 * Created by 浩 on 2015-11-06 0006.
 */
public interface Table extends Serializable {

    /**
     * 获取表元数据
     *
     * @return 元数据
     */
    TableMetaData getMetaData();

    /**
     * 创建查询器,通过查询器可对表数据进行查询操作
     *
     * @return 查询器
     * @throws QueryException 创建查询器异常
     */
    Query createQuery() throws QueryException;

    /**
     * 创建修改器,通过修改器可对表数据进行修改操作
     *
     * @return 修改器
     * @throws UpdateException 创建修改器异常
     */
    Update createUpdate() throws UpdateException;

    /**
     * 创建删除器,通过删除器可对表数据进行删除操作
     *
     * @return 删除器
     * @throws DeleteException 创建删除器异常
     */
    Delete createDelete() throws DeleteException;

    /**
     * 创建插入器，通过插入器可对表数据进行新增操作
     *
     * @return 插入器
     * @throws InsertException 创建插入器异常
     */
    Insert createInsert() throws InsertException;
}
