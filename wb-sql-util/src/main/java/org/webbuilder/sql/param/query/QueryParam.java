package org.webbuilder.sql.param.query;

import com.alibaba.fastjson.JSON;
import org.webbuilder.sql.param.ExecuteCondition;
import org.webbuilder.sql.param.SqlRenderConfig;
import org.webbuilder.sql.parser.ExecuteConditionParser;

import java.util.*;

/**
 * 查询参数
 * Created by 浩 on 2015-11-06 0006.
 */
public class QueryParam extends SqlRenderConfig {

    /**
     * 是否分页
     */
    private boolean paging = false;

    /**
     * 分页开始页码,从0开始
     */
    private int pageIndex = 0;

    /**
     * 每页数量,默认50
     */
    private int pageSize = 50;

    /**
     * 查询参数原始对象
     */
    private Map<String, Object> param = new LinkedHashMap<>();

    /**
     * 是否跳过执行触发器
     *
     * @return this 实例
     */
    public QueryParam skipTrigger() {
        this.addProperty("skipTrigger", true);
        return this;
    }

    public QueryParam() {
    }

    public QueryParam(SqlRenderConfig sqlRenderConfig) {
        super(sqlRenderConfig);
    }

    public QueryParam groupBy(String field) {
        addProperty("group_by", field);
        return this;
    }

    public QueryParam orderBy(boolean desc, String field, String... fields) {
        Set<String> orderBies = new LinkedHashSet<>();
        orderBies.add(field);
        orderBies.addAll(Arrays.asList(fields));
        addProperty("order_by", orderBies);
        addProperty("order_by_mod", desc ? "desc" : "asc");
        return this;
    }

    public QueryParam orderBy(String field) {
        orderBy(false, field);
        return this;
    }

    public void copy(QueryParam queryParam) {
        super.copy(queryParam);
        queryParam.setPaging(this.paging);
        queryParam.setPageIndex(this.getPageIndex());
        queryParam.setPageSize(this.getPageSize());
        queryParam.param = this.param;
    }

    public QueryParam(boolean paging) {
        this.paging = paging;
    }

    public QueryParam(int pageIndex, int pageSize) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        paging = true;
    }

    public boolean isPaging() {
        return paging;
    }

    public void setPaging(boolean paging) {
        this.paging = paging;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public QueryParam noPaging() {
        setPaging(false);
        return this;
    }

    public void doPaging(int pageIndex, int pageSize) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        if (pageIndex < 0) this.pageIndex = 0;
        if (pageSize <= 0) this.pageSize = 1;
        setPaging(true);
    }

    public QueryParam select(String field, String... fields) {
        this.include(field, fields);
        return this;
    }

    @Override
    public Set<ExecuteCondition> getConditions() {
        return ExecuteConditionParser.parseByMap(param);
    }

    public QueryParam where(String conditionJson) {
        param.putAll(JSON.parseObject(conditionJson));
        return this;
    }

    public QueryParam where(String key, Object value) {
        param.put(key, value);
        return this;
    }


    public QueryParam where(Map<String, Object> conditionMap) {
        param.putAll(conditionMap);
        return this;
    }

    public Map<String, Object> getParam() {
        return param;
    }

    public void setParam(Map<String, Object> param) {
        this.param = param;
    }
}
