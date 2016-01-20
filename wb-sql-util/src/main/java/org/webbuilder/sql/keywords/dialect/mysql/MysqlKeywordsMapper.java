package org.webbuilder.sql.keywords.dialect.mysql;

import org.webbuilder.sql.keywords.FieldTemplateWrapper;
import org.webbuilder.sql.keywords.dialect.AbstractKeywordsMapper;
import org.webbuilder.sql.keywords.dialect.mysql.wrapper.*;
import org.webbuilder.sql.param.ExecuteCondition;

import java.util.HashMap;
import java.util.Map;

/**
 * MYSQL关键字映射器,用于获取MYSQL专用的关键字等信息
 * Created by 浩 on 2015-11-09 0009.
 */
public class MysqlKeywordsMapper extends AbstractKeywordsMapper {

    //字段模板包装其
    private static final Map<ExecuteCondition.QueryType, FieldTemplateWrapper> wrappers = new HashMap<>();

    static {
        // =
        addWrapper(new EQWrapper(true));
        // !=
        addWrapper(new EQWrapper(false));

        // like '%?'
        addWrapper(new EndWrapper(true));
        // not like '%?'
        addWrapper(new EndWrapper(false));

        // like '?%'
        addWrapper(new StartWrapper(true));
        // not like '?%'
        addWrapper(new StartWrapper(false));
        // in (?,?)
        addWrapper(new INWrapper(true));
        // not in (?,?)
        addWrapper(new INWrapper(false));

        // like '%?%'
        addWrapper(new LIKEWrapper(true));
        // not like '%?%'
        addWrapper(new LIKEWrapper(false));

        // is null
        addWrapper(new NullWrapper(true));
        // is not null
        addWrapper(new NullWrapper(false));

        // >=
        addWrapper(new ThanWrapper(true));
        // <=
        addWrapper(new ThanWrapper(false));
    }

    public static void addWrapper(FieldTemplateWrapper wrapper) {
        wrappers.put(wrapper.getType(), wrapper);
    }

    @Override
    public FieldTemplateWrapper getFieldTemplateWrapper(ExecuteCondition.QueryType type) {
        return wrappers.get(type);
    }

    @Override
    public String getSpecifierPrefix() {
        return "`";
    }

    @Override
    public String getSpecifierSuffix() {
        return "`";
    }

    @Override
    public String pager(String sql, int pageIndex, int pageSize) {
        StringBuilder builder = new StringBuilder();
        builder.append(sql);
        builder.append(" limit ").append(pageSize * pageIndex).append(",").append(pageSize * (pageIndex + 1));
        return builder.toString();
    }

}
