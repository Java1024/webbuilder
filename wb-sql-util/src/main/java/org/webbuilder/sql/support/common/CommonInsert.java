package org.webbuilder.sql.support.common;

import org.webbuilder.sql.Constant;
import org.webbuilder.sql.Insert;
import org.webbuilder.sql.SQL;
import org.webbuilder.sql.TableMetaData;
import org.webbuilder.sql.param.insert.InsertParam;
import org.webbuilder.sql.render.template.SqlTemplate;
import org.webbuilder.sql.support.executor.SqlExecutor;
import org.webbuilder.utils.common.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 浩 on 2015-11-12 0012.
 */
public class CommonInsert extends TriggerExecutor implements Insert {
    private SqlTemplate sqlTemplate;
    private SqlExecutor sqlExecutor;

    public CommonInsert(SqlTemplate sqlTemplate, SqlExecutor sqlExecutor) {
        this.sqlTemplate = sqlTemplate;
        this.sqlExecutor = sqlExecutor;
    }

    @Override
    public boolean insert(InsertParam param) throws Exception {
        Map<String, Object> root = new HashMap<>();
        root.put("param", param);
        //尝试执行触发器
        if (!isSkipTrigger(param))
            tryExecuteTrigger(Constant.TRIGGER_INSERT_BEFORE, root);
        if (!StringUtils.isTrue(param.getProperty("skipValid")))
            //尝试验证数据
            tryInsertValidData(param.getData());
        SQL sql = sqlTemplate.render(param);
        sqlExecutor.insert(sql);
        if (!isSkipTrigger(param))
            tryExecuteTrigger(Constant.TRIGGER_INSERT_DONE, root);
        return true;
    }

    @Override
    public TableMetaData getTableMetaData() {
        return sqlTemplate.getTableMetaData();
    }
}
