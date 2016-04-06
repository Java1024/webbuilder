package org.webbuilder.sql.test;

import org.junit.Test;
import org.webbuilder.sql.*;
import org.webbuilder.sql.param.query.QueryParam;
import org.webbuilder.sql.support.OracleDataBaseMetaData;
import org.webbuilder.sql.support.common.CommonDataBase;
import org.webbuilder.sql.support.executor.AbstractJdbcSqlExecutor;
import org.webbuilder.sql.support.executor.SQLInfo;
import org.webbuilder.sql.support.executor.SqlExecutor;

import java.sql.Connection;

/**
 * Created by 浩 on 2016-01-18 0018.
 */
public class Test2 {

    @Test
    public void test2() throws Exception {
        DataBaseMetaData metaData = new OracleDataBaseMetaData();
        TableMetaData tableMetaData = new TableMetaData();
        tableMetaData.setName("user");
        tableMetaData.addField(new FieldMetaData("username", String.class, "varchar(50)"));
        tableMetaData.addField(new FieldMetaData("password", String.class, "varchar(50)"));
        metaData.addTable(tableMetaData);
        SqlExecutor sqlExecutor = new AbstractJdbcSqlExecutor() {
            @Override
            public Connection getConnection() {
                return null;
            }

            @Override
            protected void printSql(SQLInfo info) {
                System.out.println(info.getSql());
                System.out.println(info.paramsString());
            }

            @Override
            public void releaseConnection(Connection connection) {

            }
        };
        DataBase dataBase = new CommonDataBase(metaData, sqlExecutor);


        Table table = dataBase.getTable("user");
        table.createQuery().list(new QueryParam().where("username$LIKE", "admin"));

    }
}
