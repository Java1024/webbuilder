package org.webbuilder.sql.support.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webbuilder.sql.BindSQL;
import org.webbuilder.sql.SQL;
import org.webbuilder.utils.common.BeanUtils;
import org.webbuilder.utils.common.ClassUtils;
import org.webbuilder.utils.common.StringUtils;

import java.io.ByteArrayInputStream;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 基于jdbc的通用sql执行器,继承改类,实现getConnection方法,返回JDBC链接,调用其它方法即可进行sql执行
 * Created by 浩 on 2015-11-09 0009.
 */
public abstract class AbstractJdbcSqlExecutor implements SqlExecutor {

    /**
     * @return
     */
    public abstract Connection getConnection();

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final Pattern APPEND_PATTERN = Pattern.compile("(?<=\\$\\{)(.+?)(?=\\})");
    private static final Pattern PREPARED_PATTERN = Pattern.compile("(?<=#\\{)(.+?)(?=\\})");

    /**
     * 将sql模板编译为sql信息个
     * 模板语法:${}代表直接拼接sql,#{}使用预编译
     * 如: 模板参数为:{name:"张三",age:10},sql为:select * from user where name=#{name} and age=${age}
     * 将被编译为:select * from user where name=? and age=10。 参数列表:["张三"]
     *
     * @param sql sql模板
     * @return sql 信息
     */
    public static SQLInfo compileSql(SQL sql) {
        SQLInfo sqlInfo = new SQLInfo();
        String sqlTemplate = sql.getSql();
        Map<String, Object> param = sql.getParams();
        Matcher prepared_matcher = PREPARED_PATTERN.matcher(sqlTemplate);
        Matcher append_matcher = APPEND_PATTERN.matcher(sqlTemplate);
        List<Object> params = new LinkedList<>();

        //直接拼接sql
        while (append_matcher.find()) {
            String group = append_matcher.group();
            Object obj = param.get(group);
            if (obj == null)
                try {
                    obj = BeanUtils.attr(group, param);
                } catch (Exception e) {
                }
            sqlTemplate = sqlTemplate.replaceFirst(StringUtils.concat("\\$\\{", group.replace("$", "\\$"), "\\}"), String.valueOf(obj));
        }
        //参数预编译sql
        while (prepared_matcher.find()) {
            String group = prepared_matcher.group();
            sqlTemplate = sqlTemplate.replaceFirst(StringUtils.concat("#\\{", group.replace("$", "\\$"), "\\}"), "?");
            Object obj = param.get(group);
            if (obj == null)
                try {
                    obj = BeanUtils.attr(group, param);
                } catch (Exception e) {
                }
            params.add(obj);
        }
        sqlInfo.setSql(sqlTemplate);
        sqlInfo.setParam(params.toArray());
        return sqlInfo;
    }

    /**
     * 释放连接
     */
    public abstract void releaseConnection(Connection connection);

    @Override
    public <T> List<T> list(SQL sql, ObjectWrapper<T> wrapper) throws Exception {
        //将sql模板编译为可执行的sql
        SQLInfo info = compileSql(sql);
        printSql(info);//打印sql信息
        Connection connection = getConnection();
        //预编译SQL
        PreparedStatement statement = connection.prepareStatement(info.getSql());
        this.preparedParam(statement, info);
        //执行sql
        ResultSet resultSet = statement.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int count = metaData.getColumnCount();
        //获取到执行sql后返回的列信息
        List<String> headers = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            headers.add(metaData.getColumnLabel(i));
        }
        int index = 0;
        List<T> datas = new ArrayList<>();
        while (resultSet.next()) {
            //调用包装器,将查询结果包装为对象
            T data = wrapper.newInstance();
            for (String header : headers) {
                Object value = resultSet.getObject(header);
                wrapper.wrapper(data, index, header, value);
            }
            index++;
            wrapper.done(data);
            datas.add(data);
        }
        closeResultSet(resultSet);
        closeStatement(statement);
        //重置JDBC链接
        releaseConnection(connection);
        return datas;
    }

    protected void closeResultSet(ResultSet resultSet) {
        try {
            resultSet.close();
        } catch (SQLException e) {
            logger.error("close ResultSet error", e);
        }
    }

    protected void closeStatement(Statement statement) {
        try {
            statement.close();
        } catch (SQLException e) {
            logger.error("close ResultSet error", e);
        }
    }

    @Override
    public <T> T single(SQL sql, ObjectWrapper<T> wrapper) throws Exception {
        SQLInfo info = compileSql(sql);
        printSql(info);
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(info.getSql());
        //预编译参数
        this.preparedParam(statement, info);
        ResultSet resultSet = statement.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int count = metaData.getColumnCount();
        List<String> headers = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            headers.add(metaData.getColumnLabel(i));
        }
        int index = 0;
        T data = null;
        if (resultSet.next()) {
            data = wrapper.newInstance();
            for (String header : headers) {
                Object value = resultSet.getObject(header);
                wrapper.wrapper(data, index, header, value);
            }
            index++;
            wrapper.done(data);
        }
        closeResultSet(resultSet);
        closeStatement(statement);
        releaseConnection(connection);
        return data;
    }

    @Override
    public void exec(SQL sql) throws Exception {
        SQLInfo info = compileSql(sql);
        printSql(info);
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(info.getSql());
        //预编译参数
        this.preparedParam(statement, info);
        statement.execute();
        if (sql.getBinds() != null) {
            for (BindSQL bindSQL : sql.getBinds()) {
                exec(bindSQL.getSql());
            }
        }
        closeStatement(statement);
        releaseConnection(connection);
    }

    @Override
    public int update(SQL sql) throws Exception {
        SQLInfo info = compileSql(sql);
        printSql(info);
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(info.getSql());
        this.preparedParam(statement, info);
        int i = statement.executeUpdate();
        if (logger.isDebugEnabled())
            logger.debug("{} rows is updated!", i);
        closeStatement(statement);
        releaseConnection(connection);
        return i;
    }

    @Override
    public int delete(SQL sql) throws Exception {
        SQLInfo info = compileSql(sql);
        printSql(info);
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(info.getSql());
        this.preparedParam(statement, info);
        int i = statement.executeUpdate();
        if (sql.getBinds() != null) {
            for (BindSQL bindSQL : sql.getBinds()) {
                i += delete(bindSQL.getSql());
            }
            return i;
        }
        logger.debug("{} rows is delete!", i);
        closeStatement(statement);
        releaseConnection(connection);
        return i;
    }

    @Override
    public int insert(SQL sql) throws Exception {
        exec(sql);
        return sql.size();
    }

    /**
     * 预编译参数
     *
     * @param statement
     * @param info
     * @throws Exception
     */
    protected void preparedParam(PreparedStatement statement, SQLInfo info) throws Exception {
        int index = 1;
        //预编译参数
        for (Object object : info.getParam()) {
            if (object instanceof Date)
                statement.setTimestamp(index++, new java.sql.Timestamp(((Date) object).getTime()));
            else if (object instanceof byte[]) {
                statement.setBlob(index++, new ByteArrayInputStream((byte[]) object));
            } else
                statement.setObject(index++, object);
        }
    }

    protected void printSql(SQLInfo info) {
        if (logger.isDebugEnabled()) {
            logger.debug("execute sql :" + info.getSql());
            logger.debug("params :" + info.paramsString());
        }
    }
}
