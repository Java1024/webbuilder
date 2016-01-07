package org.webbuilder.web.service.basic;

import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;
import org.webbuilder.sql.support.executor.AbstractJdbcSqlExecutor;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Created by 浩 on 2015-10-09 0009.
 */
@Service
public class SqlExecutorService extends AbstractJdbcSqlExecutor {

    @Resource
    private DataSource dataSource;

    @Override
    public Connection getConnection() {
        try {
            return DataSourceUtils.getConnection(dataSource);
        } catch (Exception e) {
            logger.error("获取数据库连接失败", e);
        }
        return null;
    }

    @Override
    public void resetConnection(Connection connection) {
         DataSourceUtils.releaseConnection(connection, dataSource);
    }
}
