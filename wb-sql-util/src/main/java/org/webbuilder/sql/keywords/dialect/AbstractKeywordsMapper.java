package org.webbuilder.sql.keywords.dialect;


import org.webbuilder.sql.keywords.FieldTemplateWrapper;
import org.webbuilder.sql.keywords.KeywordsMapper;
import org.webbuilder.sql.param.ExecuteCondition;
import org.webbuilder.sql.param.IncludeField;
import org.webbuilder.sql.param.WrapperCondition;
import org.webbuilder.sql.param.query.GroupBy;
import org.webbuilder.sql.param.query.OrderBy;
import org.webbuilder.utils.common.StringUtils;

import java.util.*;

/**
 * 抽象关键字映射器
 * Created by 浩 on 2015-11-09 0009.
 */
public abstract class AbstractKeywordsMapper implements KeywordsMapper {

    //支持的函数
    private final Map<String, String> methods = new HashMap<>();

    public AbstractKeywordsMapper() {
        methods.put("count", "count(%s)");
        methods.put("sum", "sum(%s)");
        methods.put("avg", "avg(%s)");
    }

    @Override
    public WrapperCondition wrapperCondition(ExecuteCondition executeCondition) {
        WrapperCondition condition = new WrapperCondition();
        Set<String> tables = new LinkedHashSet<>();
        tables.add(executeCondition.getTable());
        if (!executeCondition.getTableMetaData().hasField(executeCondition.getFullField())
                && !executeCondition.getTableMetaData().hasCorrelation(executeCondition.getTable()))
            return null;
        //模板
        FieldTemplateWrapper wrapper = getFieldTemplateWrapper(executeCondition.getQueryType());
        String template = wrapper.template(executeCondition);
        Map<String, Object> params = new LinkedHashMap<>();
        Map<String, Object> value = wrapper.value(executeCondition);
        params.putAll(value);
        Set<ExecuteCondition> nest = executeCondition.getNest();
        StringBuilder real = new StringBuilder(executeCondition.getAppendType()).append(" ");
        //组合关联判断
        if (nest.size() != 0) {
            real.append("(");
            real.append(template);
            for (ExecuteCondition entry : nest) {
                entry.setTableMetaData(executeCondition.getTableMetaData());
                if (entry.getTable() == null)
                    entry.setTable(executeCondition.getTable());
                real.append(" ");
                WrapperCondition tmp = wrapperCondition(entry);
                if (tmp == null) continue;
                real.append(tmp.getTemplate());
                params.putAll(tmp.getParams());
                tables.addAll(tmp.getTables());
            }
            real.append(")");
        } else {
            real.append(template);
        }
        condition.setTables(tables);
        condition.setTemplate(real.toString());
        condition.setParams(params);
        return condition;
    }


    @Override
    public String getFieldTemplate(IncludeField include) {
        if (include instanceof OrderBy) {//如果字段是order by
            return orderBy((OrderBy) include);
        } else if (include instanceof GroupBy) {
            return groupBy((GroupBy) include); //如果字段是group by
        } else {
            return select(include);
        }
    }

    protected String select(IncludeField include) {
        StringBuilder builder = new StringBuilder();
        if (include.getMethod() != null) {
            String tmp = methods.get(include.getMethod());
            if (tmp != null) {
                if (include.isSkipCheck())
                    builder.append(String.format(tmp, include.getField()));
                else
                    builder.append(String.format(tmp, include.getFullField()));
            }
        } else {
            builder.append(include.getFullField());
        }
        builder.append(" as ");
        builder.append(getSpecifierPrefix());
        if (include.isAnotherTable() && !include.isSkipCheck()) {
            builder.append(include.getTargetTable()).append(".");
        }
        builder.append(include.getAs());
        builder.append(getSpecifierSuffix());
        return builder.toString();
    }

    protected String groupBy(GroupBy groupBy) {
        StringBuilder builder = new StringBuilder();
        String field = groupBy.getFullField();
        if (StringUtils.isNullOrEmpty(groupBy.getMethod())) {
            builder.append(field);
        }
        return builder.toString();
    }

    protected String orderBy(OrderBy orderBy) {
        StringBuilder builder = new StringBuilder();
        String field = orderBy.getFullField();
        if (StringUtils.isNullOrEmpty(orderBy.getMethod())) {
            builder.append(field);
        }
        return builder.toString();
    }


}
