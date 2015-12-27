package org.webbuilder.sql.support.common.mysql;

import org.webbuilder.sql.BindSQL;
import org.webbuilder.sql.FieldMetaData;
import org.webbuilder.sql.SQL;
import org.webbuilder.sql.TableMetaData;
import org.webbuilder.sql.exception.SqlRenderException;
import org.webbuilder.sql.param.SqlAppender;
import org.webbuilder.sql.param.SqlRenderConfig;
import org.webbuilder.sql.render.template.SqlTemplate;
import org.webbuilder.sql.support.common.CommonSql;
import org.webbuilder.utils.common.StringUtils;

import java.util.*;

/**
 * Created by 浩 on 2015-11-17 0017.
 */
public class MysqlTableCreateRender implements SqlTemplate {

    private TableMetaData tableMetaData;

    private List<SqlAppender> template = new ArrayList<>();

    public MysqlTableCreateRender(TableMetaData tableMetaData) {
        this.tableMetaData = tableMetaData;
    }

    @Override
    public TableMetaData getTableMetaData() {
        return tableMetaData;
    }

    @Override
    public String getTemplate() {
        return template.toString();
    }

    @Override
    public TYPE getType() {
        return TYPE.CREATE;
    }

    @Override
    public SQL render(SqlRenderConfig config) {
        CommonSql commonSql = new CommonSql();
        for (int i = 0; i < template.size(); i++) {
            SqlAppender sqlAppender = template.get(i);
            if (i == 0) {
                commonSql.setSql(sqlAppender.toString());
            } else {
                BindSQL bindSQL = new BindSQL();
                bindSQL.setToField("__");
                bindSQL.setSql(new CommonSql(sqlAppender.toString()));
                commonSql.getBinds().add(bindSQL);
            }
        }
        return commonSql;
    }

    @Override
    public void reload() throws SqlRenderException {
        SqlAppender appender = new SqlAppender();
        appender.addSpc("create", "table", tableMetaData.getName(), "(");
        boolean isFirst = true;
        Set<String> primarykeys = new LinkedHashSet<>();

        for (FieldMetaData fieldMetaData : tableMetaData.getFields()) {
            if (!isFirst) {
                appender.addEdSpc(",");
            }
            appender.add("`", fieldMetaData.getName(), "` ").addSpc(fieldMetaData.getDataType());
            if (fieldMetaData.isNotNull()) {
                appender.addSpc("not null");
            }
            isFirst = false;
            //注释
            if (!StringUtils.isNullOrEmpty(fieldMetaData.getComment())) {
                appender.addSpc(String.format(" COMMENT '%s'", fieldMetaData.getComment()));
            }
            //主键
            if (fieldMetaData.isPrimaryKey()) {
                primarykeys.add(fieldMetaData.getName());
            }
        }
        if (primarykeys.size() != 0) {
            appender.addSpc(",", "primary key", "(");
            boolean first = true;
            for (String primarykey : primarykeys) {
                if (!first)
                    appender.add(",");
                first = false;
                appender.add("`", primarykey, "`");
            }
            appender.addEdSpc(")");
        }
        appender.addEdSpc(")");
        if (tableMetaData.getComment() != null) {
            appender.add("COMMENT=", "'", tableMetaData.getComment(), "'");
        }
        template.add(appender);
    }

}
