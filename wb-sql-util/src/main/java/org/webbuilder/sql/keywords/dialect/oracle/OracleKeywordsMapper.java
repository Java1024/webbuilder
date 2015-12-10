package org.webbuilder.sql.keywords.dialect.oracle;

import org.webbuilder.sql.keywords.FieldTemplateWrapper;
import org.webbuilder.sql.keywords.dialect.AbstractKeywordsMapper;
import org.webbuilder.sql.keywords.dialect.oracle.wrapper.*;
import org.webbuilder.sql.param.ExecuteCondition;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 浩 on 2015-11-09 0009.
 */
public class OracleKeywordsMapper extends AbstractKeywordsMapper {

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

    @Override
    protected FieldTemplateWrapper getQueryTypeMapper(ExecuteCondition.QueryType type) {
        return wrappers.get(type);
    }

    public static void addWrapper(FieldTemplateWrapper wrapper) {
        wrappers.put(wrapper.getType(), wrapper);
    }


    @Override
    public String getSpecifierPrefix() {
        return "\"";
    }

    @Override
    public String getSpecifierSuffix() {
        return "\"";
    }

    @Override
    public String pager(String sql, int pageIndex, int pageSize) {
        StringBuilder builder = new StringBuilder("SELECT * FROM ( SELECT row_.*, rownum rownum_ FROM (");
        builder.append(sql);
        builder.append(") row_ )");
        builder.append("WHERE rownum_ <= ").append(pageSize * (pageIndex + 1)).append(" and rownum_ > ").append(pageSize * pageIndex);
        return builder.toString();
    }

}
