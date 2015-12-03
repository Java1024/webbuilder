package org.webbuilder.web.core.bean;

import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 自定义响应json数据
 * Created by 浩 on 2015-08-02 0002.
 */
public class ResponseData {
    /**
     * 要响应的数据
     */
    private Object data;

    /**
     * 是否支持jsoup跨域回掉
     */
    private String callBack;

    /**
     * fastjson序列化过滤器
     */
    private Set<SerializeFilter> filters = new HashSet<>();

    public ResponseData() {

    }

    public ResponseData(Object data) {
        this.data = data;
    }

    public ResponseData addFilter(SerializeFilter serializeFilter) {
        filters.add(serializeFilter);
        return this;
    }

    public Object getData() {
        return data;
    }

    public ResponseData setData(Object data) {
        this.data = data;
        return this;
    }

    public Set<SerializeFilter> getFilters() {
        return filters;
    }

    public void setFilters(Set<SerializeFilter> filters) {
        this.filters = filters;
    }

    /**
     * 过滤指定类中不进行序列化的字段
     *
     * @param type     要进行过滤类型
     * @param excludes 不序列化的字段
     * @return this 引用
     */
    public ResponseData excludes(Class type, String... excludes) {
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter(type);
        filter.getExcludes().addAll(Arrays.asList(excludes));
        filters.add(filter);
        return this;
    }

    /**
     * 过滤指定类型中只序列化哪些字段
     *
     * @param type     要进行过滤类型
     * @param excludes 只进行序列化的列表
     * @return this 引用
     */
    public ResponseData includes(Class type, String... excludes) {
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter(type);
        filter.getIncludes().addAll(Arrays.asList(excludes));
        filters.add(filter);
        return this;
    }

    public String getCallBack() {
        return callBack;
    }

    /**
     * 设置jsoup回掉
     *
     * @param callBack 回掉
     * @return this 引用
     */
    public ResponseData setCallBack(String callBack) {
        this.callBack = callBack;
        return this;
    }
}
