package org.webbuilder.sql.support;

import org.webbuilder.sql.DataBaseMetaData;
import org.webbuilder.sql.TableMetaData;
import org.webbuilder.sql.keywords.KeywordsMapper;
import org.webbuilder.sql.keywords.dialect.mysql.MysqlKeywordsMapper;
import org.webbuilder.sql.keywords.dialect.oracle.OracleKeywordsMapper;
import org.webbuilder.sql.render.template.SqlTemplateRender;
import org.webbuilder.sql.support.common.CommonSqlTemplateRender;
import org.webbuilder.sql.support.common.mysql.MysqlTableAlterRender;
import org.webbuilder.sql.support.common.mysql.MysqlTableCreateRender;
import org.webbuilder.sql.support.common.oracle.OracleTableAlterRender;
import org.webbuilder.sql.support.common.oracle.OracleTableCreateRender;

/**
 * MYSQL数据库支持
 * Created by 浩 on 2015-11-17 0017.
 */
public class MysqlDataBaseMetaData extends DataBaseMetaData {

    //初始化sql模板渲染器,使用通用的sql模板,另外提供MYSQL专用的表结构处理模板渲染器
    protected SqlTemplateRender sqlTemplateRender = new CommonSqlTemplateRender() {
        @Override
        public void init(TableMetaData tableMetaData) {
            super.init(tableMetaData);
            cacheTemplate(tableMetaData.getName(), new MysqlTableAlterRender(tableMetaData));

            cacheTemplate(tableMetaData.getName(), new MysqlTableCreateRender(tableMetaData));
        }
    };
    //mysql 关键字映射
    protected KeywordsMapper keywordsMapper = new MysqlKeywordsMapper();

    protected String name = "mysql";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public SqlTemplateRender getRender() {
        return sqlTemplateRender;
    }

    @Override
    public KeywordsMapper getKeywordsMapper() {
        return keywordsMapper;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setKeywordsMapper(KeywordsMapper keywordsMapper) {
        this.keywordsMapper = keywordsMapper;
    }

    public void setSqlTemplateRender(SqlTemplateRender sqlTemplateRender) {
        this.sqlTemplateRender = sqlTemplateRender;
    }
}

