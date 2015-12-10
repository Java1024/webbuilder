package org.webbuilder.sql;

import org.webbuilder.sql.param.delete.DeleteParam;

/**
 * 删除器,用于对表数据进行删除操作
 * Created by 浩 on 2015-11-06 0006.
 */
public interface Delete {

    /**
     * 通过删除参数,执行删除操作
     *
     * @param param 删除参数
     * @return 执行操作影响的数据行数
     * @throws Exception 执行异常
     */
    int delete(DeleteParam param) throws Exception;
}
