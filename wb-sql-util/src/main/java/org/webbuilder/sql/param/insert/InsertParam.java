package org.webbuilder.sql.param.insert;

import org.webbuilder.sql.param.IncludeField;
import org.webbuilder.sql.param.SqlRenderConfig;
import org.webbuilder.utils.common.BeanUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by 浩 on 2015-11-12 0012.
 */
public class InsertParam extends SqlRenderConfig {

    private Map<String, Object> data = new LinkedHashMap<>();

    @Override
    public Set<IncludeField> getIncludes() {
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            this.include(new InsertField(entry.getKey(), entry.getValue()));
        }
        return super.getIncludes();
    }

    public InsertParam value(String field, Object value) {
        data.put(field, value);
        return this;
    }

    public InsertParam values(Map<String, Object> data) {
        this.data.putAll(data);
        return this;
    }

    public InsertParam values(Object o) {
        values(BeanUtils.copy(o,new LinkedHashMap<String, Object>()));
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public InsertParam skipTrigger() {
        this.addProperty("skipTrigger", true);
        return this;
    }
}
