package org.webbuilder.sql;

import java.io.Serializable;
import java.util.*;

/**
 * 字段元数据类
 * Created by 浩 on 2015-11-06 0006.
 */
public class FieldMetaData implements Serializable {

    /**
     * 表元数据
     */
    private TableMetaData tableMetaData;

    /**
     * 字段名称
     */
    private String name;

    /**
     * 字段别名
     */
    private String alias;

    /**
     * 字段备注
     */
    private String comment;

    /**
     * 字段的java类型,默认为String类型
     */
    private Class<?> javaType;

    /**
     * 字段数据库类型,如 varchar,datetime等，(需要与使用的数据库对应)
     */
    private String dataType;

    /**
     * 字段长度
     */
    private int length;

    /**
     * 是否不能为null
     */
    private boolean notNull;

    /**
     * 是否为主键
     */
    private boolean primaryKey;

    /**
     * 默认值
     */
    private Object defaultValue;

    /**
     * 验证器
     */
    private Set<String> validator = new LinkedHashSet<>();

    /**
     * 字段是否可进行修改
     */
    private boolean canUpdate = true;

    /**
     * 字段的自定义属性
     */
    private Map<String, Object> attr = new LinkedHashMap<>();


    public FieldMetaData() {
    }

    public FieldMetaData(String name, Class<?> javaType, String dataType) {
        this.name = name;
        this.javaType = javaType;
        this.dataType = dataType;
    }

    /**
     * 设置字段的自定义属性
     *
     * @param key  属性名称
     * @param attr 属性值
     * @return 设置的值
     */
    public Object attr(String key, Object attr) {
        this.attr.put(key, attr);
        return attr;
    }

    /**
     * 设置多个字段的自定义属性
     *
     * @param attr 属性列表
     * @return 字段的全部自定义属性
     */
    public Map<String, Object> attr(Map<String, Object> attr) {
        this.attr.putAll(attr);
        return this.attr;
    }

    /**
     * 获取一个自定义属性值
     *
     * @param key 属性名称
     * @return 属性值
     */
    public Object attr(String key) {
        return attr.get(key);
    }

    /**
     * 获取所有自定义属性
     *
     * @return 所有自定义属性
     */
    public Map<String, Object> attr() {
        return attr;
    }

    /**
     * 删除一个自定义属性
     *
     * @param attr 属性名称
     * @return 被删除的属性值
     */
    public Object removeAttr(String attr) {
        return this.attr.remove(attr);
    }

    public boolean isCanUpdate() {
        return canUpdate;
    }

    public void setCanUpdate(boolean canUpdate) {
        this.canUpdate = canUpdate;
    }

    public String getFullName() {
        return getTableMetaData().getName().concat(".").concat(getName());
    }

    public TableMetaData getTableMetaData() {
        return tableMetaData;
    }

    public void setTableMetaData(TableMetaData tableMetaData) {
        this.tableMetaData = tableMetaData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Class<?> getJavaType() {
        if (javaType == null) return String.class;
        return javaType;
    }

    public void setJavaType(Class<?> javaType) {
        this.javaType = javaType;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean isNotNull() {
        return notNull;
    }

    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Set<String> getValidator() {
        return validator;
    }

    public void setValidator(Set<String> validator) {
        this.validator = validator;
    }

    public FieldMetaData addValidator(String validator) {
        this.validator.add(validator);
        return this;
    }
}
