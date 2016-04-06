package org.webbuilder.sql.support.common;

import org.webbuilder.sql.*;
import org.webbuilder.sql.exception.DeleteException;
import org.webbuilder.sql.exception.InsertException;
import org.webbuilder.sql.exception.QueryException;
import org.webbuilder.sql.exception.UpdateException;
import org.webbuilder.sql.render.template.SqlTemplate;
import org.webbuilder.sql.render.template.SqlTemplateRender;
import org.webbuilder.sql.support.executor.*;

import java.util.Map;

/**
 * 通用表操作类
 * Created by 浩 on 2015-11-09 0009.
 */
public class CommonTable implements Table {
    /**
     * 默认对象包装器
     */
    private final ObjectWrapper DEFAULT_WRAPPER;

    /**
     * 表元数据
     */
    private TableMetaData metaData;

    /**
     * sql执行器
     */
    private SqlExecutor sqlExecutor;

    //
    private DataBase dataBase;

    private Query query;
    private Update update;
    private Delete delete;
    private Insert insert;

    private ObjectWrapperFactory wrapperFactory;

    public CommonTable(TableMetaData metaData, SqlExecutor sqlExecutor, DataBase dataBase, ObjectWrapperFactory wrapperFactory) {
        this.metaData = metaData;
        this.sqlExecutor = sqlExecutor;
        this.dataBase = dataBase;
        this.wrapperFactory = wrapperFactory;
        if (wrapperFactory != null) {
            DEFAULT_WRAPPER = wrapperFactory.getWrapper(dataBase, this);
        } else {
            DEFAULT_WRAPPER = new HashMapWrapper();
        }
    }

    @Override
    public TableMetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(TableMetaData metaData) {
        this.metaData = metaData;
    }

    @Override
    public Query createQuery() throws QueryException {
        //单例
        if (query != null) return query;
        SqlTemplate template = metaData.getTemplate(SqlTemplate.TYPE.SELECT);
        CommonQuery query = new CommonQuery(template, sqlExecutor);
        query.setDataBase(dataBase);
        query.setTable(this);
        //注册wrapper触发器
        ScriptObjectWrapper wrapper = new ScriptObjectWrapper(metaData, DEFAULT_WRAPPER);
        wrapper.setTable(this);
        wrapper.setDataBase(dataBase);
        query.setObjectWrapper(wrapper);
        return this.query = query;
    }

    @Override
    public Update createUpdate() throws UpdateException {
        if (update != null) return update;
        SqlTemplate template = metaData.getTemplate(SqlTemplate.TYPE.UPDATE);
        CommonUpdate update = new CommonUpdate(template, sqlExecutor);
        update.setTable(this);
        update.setDataBase(dataBase);
        return this.update = update;
    }

    @Override
    public Delete createDelete() throws DeleteException {
        if (delete != null) return delete;
        SqlTemplate template = metaData.getTemplate(SqlTemplate.TYPE.DELETE);
        CommonDelete delete = new CommonDelete(template, sqlExecutor);
        delete.setTable(this);
        delete.setDataBase(dataBase);
        return this.delete = delete;
    }

    @Override
    public Insert createInsert() throws InsertException {
        if (insert != null) return insert;
        SqlTemplate template = metaData.getTemplate(SqlTemplate.TYPE.INSERT);
        CommonInsert insert = new CommonInsert(template, sqlExecutor);
        insert.setTable(this);
        insert.setDataBase(dataBase);
        return this.insert = insert;
    }
}
