package org.webbuilder.sql;

import org.webbuilder.sql.keywords.KeywordsMapper;
import org.webbuilder.sql.render.template.SqlRenderParam;
import org.webbuilder.sql.render.template.SqlTemplate;
import org.webbuilder.sql.render.template.SqlTemplateRender;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据库元数据抽象类,继承该类,以实现不同数据库的处理
 * Created by 浩 on 2015-11-06 0006.
 */
public abstract class DataBaseMetaData {

    /**
     * 表信息
     */
    private Map<String, TableMetaData> tables = new ConcurrentHashMap<>();

    /**
     * 获取所有表信息
     *
     * @return 表信息
     */
    public Set<TableMetaData> showTables() {
        return new HashSet<>(tables.values());
    }

    /**
     * 向数据库添加一个表元数据,并初始化
     *
     * @param tableMetaData 表元数据
     * @return 添加后的表元数据
     */
    public TableMetaData addTable(TableMetaData tableMetaData) {
        tableMetaData.setDataBaseMetaData(this);
        tables.put(tableMetaData.getName(), tableMetaData);
        getRender().init(tableMetaData);
        return tableMetaData;
    }

    /**
     * 获取一个表的元数据
     *
     * @param tableName 表名
     * @return 表的元数据, 未获取到返回null
     */
    public TableMetaData getTableMetaData(String tableName) {
        return tables.get(tableName);
    }

    /**
     * 获取数据库名称
     *
     * @return 数据库名称
     */
    public abstract String getName();

    /**
     * 获取sql模板渲染器
     *
     * @return sql模板渲染器
     */
    public abstract SqlTemplateRender getRender();

    /**
     * 获取关键字映射器
     *
     * @return 关键字映射器
     */
    public abstract KeywordsMapper getKeywordsMapper();

    /**
     * 渲染一个指定类型的sql模板
     *
     * @param type          sql类型
     * @param tableMetaData 表元数据
     * @return sql模板
     */
    public SqlTemplate getTemplate(SqlTemplate.TYPE type, TableMetaData tableMetaData) {
        SqlTemplateRender render = this.getRender();
        SqlRenderParam param = new SqlRenderParam();
        param.setTableMetaData(tableMetaData);
        param.setType(type);
        return render.render(param);
    }

}
