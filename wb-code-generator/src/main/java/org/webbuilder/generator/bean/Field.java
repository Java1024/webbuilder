package org.webbuilder.generator.bean;

import org.webbuilder.utils.common.StringUtils;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by 浩 on 2015-07-27 0027.
 */
public class Field {

    private static final Map<String, String> jdbcTypeMapper = new LinkedHashMap<>();

    static {
        jdbcTypeMapper.put("String", "VARCHAR");
        jdbcTypeMapper.put("int", "INTEGER");
        jdbcTypeMapper.put("long", "BIGINT");
        jdbcTypeMapper.put("double", "DOUBLE");
        jdbcTypeMapper.put("boolean", "INTEGER");
        jdbcTypeMapper.put("java.util.Date", "TIMESTAMP");
    }

    private String name;

    private String javaTypeName;

    private String dataType;

    private String remark;

    private String defaultValue;

    private boolean primaryKey;

    private boolean notNull;

    private String getMethodName;
    private String setMethodName;

    private boolean readOnly;

    private boolean canSearch = true;

    private boolean list = true;

    private String jdbcType;

    private String javaTypeNameSample;

    public Map toMap() {
        Map data = new LinkedHashMap();
        data.put("名称", getName());
        data.put("java类", getJavaTypeName());
        data.put("数据类型", getDataType());
        data.put("备注", getRemark());
        data.put("默认值", getDefaultValue());
        data.put("主键", isPrimaryKey());
        data.put("不能为空", isNotNull());
        data.put("是否只读", isReadOnly());
        data.put("是搜索条件", isCanSearch());
        return data;
    }

    public Field() {
    }

    public String getJdbcType() {
        if (jdbcType == null) {
            jdbcType = jdbcTypeMapper.get(getJavaTypeName());
            if (jdbcType == null)
                jdbcType = "VARCHAR";
        }
        return jdbcType;
    }

    public void setJdbcType(String jdbcType) {
        this.jdbcType = jdbcType;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Field(String name, String dataType, String remark) {
        this.name = name;
        this.dataType = dataType;
        this.remark = remark;
    }

    public boolean isCanSearch() {
        return canSearch;
    }

    public void setCanSearch(boolean canSearch) {
        this.canSearch = canSearch;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public String getJavaTypeName() {
        return javaTypeName;
    }

    public void setJavaTypeName(String javaTypeName) {
        this.javaTypeName = javaTypeName;
    }

    public String getGetMethodName() {
        String st = "get";
        if ("boolean".equals(getJavaTypeName())) {
            st = "is";
        }
        getMethodName = st + StringUtils.toUpperCaseFirstOne(getName());
        return getMethodName;
    }

    public void setGetMethodName(String getMethodName) {
        this.getMethodName = getMethodName;
    }

    public String getSetMethodName() {
        return "set" + StringUtils.toUpperCaseFirstOne(getName());
    }

    public void setSetMethodName(String setMethodName) {
        this.setMethodName = setMethodName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public boolean isNotNull() {
        return notNull;
    }

    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void valid() throws Exception {
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public boolean isList() {
        return list;
    }

    public void setList(boolean list) {
        this.list = list;
    }

    public String getJavaTypeNameSample() {
        if (javaTypeNameSample == null) {
            if (Arrays.asList("int", "double", "long", "float", "boolean").contains(getJavaTypeName().toLowerCase())) {
                javaTypeNameSample = "number";
            } else if (Arrays.asList("date", "java.util.date").contains(getJavaTypeName().toLowerCase())) {
                javaTypeNameSample = "date";
            } else {
                javaTypeNameSample = "string";
            }
        }
        return javaTypeNameSample;
    }

    public void setJavaTypeNameSample(String javaTypeNameSample) {
        this.javaTypeNameSample = javaTypeNameSample;
    }
}
