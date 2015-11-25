package org.webbuilder.sql.support.common.mysql;

import org.webbuilder.sql.BindSQL;
import org.webbuilder.sql.FieldMetaData;
import org.webbuilder.sql.SQL;
import org.webbuilder.sql.TableMetaData;
import org.webbuilder.sql.exception.SqlRenderException;
import org.webbuilder.sql.param.SqlAppender;
import org.webbuilder.sql.param.SqlRenderConfig;
import org.webbuilder.sql.param.alter.AlterParam;
import org.webbuilder.sql.render.template.SqlTemplate;
import org.webbuilder.sql.support.common.CommonSql;
import org.webbuilder.utils.base.DateTimeUtils;
import org.webbuilder.utils.base.StringUtil;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by 浩 on 2015-11-18 0018.
 */
public class MysqlTableAlterRender implements SqlTemplate {

    private TableMetaData tableMetaData;

    public MysqlTableAlterRender(TableMetaData tableMetaData) {
        this.tableMetaData = tableMetaData;
    }

    public void setTableMetaData(TableMetaData tableMetaData) {
        this.tableMetaData = tableMetaData;
    }

    @Override
    public TableMetaData getTableMetaData() {
        return tableMetaData;
    }

    @Override
    public String getTemplate() {
        return "";
    }

    @Override
    public TYPE getType() {
        return TYPE.ALTER;
    }

    @Override
    public SQL render(SqlRenderConfig config) {
        AlterParam alterParam = ((AlterParam) config);
        TableMetaData newTable = alterParam.getNewTable();
        List<SqlAppender> newFieldSqlList = new LinkedList<>();
        List<SqlAppender> removeFieldSqlList = new LinkedList<>();
        List<SqlAppender> changedSqlList = new LinkedList<>();

        for (FieldMetaData newField : newTable.getFields()) {
            //新增的字段
            if (!tableMetaData.hasField(newField.getName())) {
                SqlAppender newFieldSql = new SqlAppender();
                newFieldSql.addSpc(String.format("add column `%s`", newField.getName()));
                newFieldSql.addSpc(newField.getDataType());
                boolean nul = true;
                if (!StringUtil.isNullOrEmpty(newField.getDefaultValue())) {
                    newFieldSql.addSpc("default", String.format("'%s'", newField.getDefaultValue()));
                    nul = false;
                }
                if (newField.isNotNull()) {
                    newFieldSql.addEdSpc("not null");
                    nul = false;
                }
                if (nul) {
                    newFieldSql.addEdSpc("null");
                }
                if (newField.getComment() != null) {
                    newFieldSql.addSpc("comment", String.format("'%s'", newField.getComment()));
                }
                newFieldSqlList.add(newFieldSql);
            } else {
                FieldMetaData old = tableMetaData.getField(newField.getName());
                if (!old.getDataType().equals(newField.getDataType())
                        || (old.isNotNull() != newField.isNotNull())
                        || (String.valueOf(old.getComment()).equals(String.valueOf(newField.getComment())))) {
                    SqlAppender changed = new SqlAppender();
                    changed.addSpc(String.format("change `%s`  `%s`", newField.getName(), newField.getName()));
                    changed.addSpc(newField.getDataType());
                    boolean nul = true;
                    if (!StringUtil.isNullOrEmpty(newField.getDefaultValue())) {
                        changed.addSpc("default", String.format("'%s'", newField.getDefaultValue()));
                        nul = false;
                    }
                    if (old.isNotNull()) {
                        changed.addEdSpc("not null");
                        nul = false;
                    }
                    if (nul) {
                        changed.addEdSpc("null");
                    }
                    if (old.getComment() != null) {
                        changed.addSpc("comment", String.format("'%s'", newField.getComment()));
                    }
                    changedSqlList.add(changed);
                }
            }
        }
        //执行删除多余的字段
        if (alterParam.isRemoveField()) {
            for (FieldMetaData old : tableMetaData.getFields()) {
                //新表中不包含字段但是在旧表中包含,主键不能删除
                if (!old.isPrimaryKey() && !newTable.hasField(old.getName())) {
                    SqlAppender droped = new SqlAppender();
                    droped.addSpc(String.format("drop column `%s`", old.getName()));
                    removeFieldSqlList.add(droped);
                }
            }
        }
        List<SqlAppender> allSql = new LinkedList<>();
        allSql.add(new SqlAppender().addSpc(String.format("ALTER TABLE `%s`", getTableMetaData().getName())));
        allSql.addAll(removeFieldSqlList);
        allSql.addAll(newFieldSqlList);
        allSql.addAll(changedSqlList);
        //"alter table ", tableMetaData.getName(),
        allSql.add(new SqlAppender().addSpc(
                String.format("comment='%s,更新于:%s。新增字段%d,删除字段%d,变更字段%d'", tableMetaData.getComment(),
                        DateTimeUtils.format(new Date(), DateTimeUtils.YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
                        , newFieldSqlList.size(), removeFieldSqlList.size(), changedSqlList.size())));

        CommonSql commonSql = new CommonSql();
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < allSql.size(); i++) {
            SqlAppender sql = allSql.get(i);
            if (i > 1) {
                builder.append(",");
            }
            builder.append(sql.toString());
        }
        commonSql.setSql(builder.toString());
        return commonSql;
    }

    @Override
    public void reload() throws SqlRenderException {

    }
}
