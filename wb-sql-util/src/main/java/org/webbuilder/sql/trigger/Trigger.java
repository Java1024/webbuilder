package org.webbuilder.sql.trigger;

import org.webbuilder.sql.exception.TriggerException;

import java.io.Serializable;
import java.util.Map;

/**
 * 触发器接口
 * Created by 浩 on 2015-11-14 0014.
 */
public interface Trigger extends Serializable {
    /**
     * 获取触发器名称
     *
     * @return 触发器名称
     */
    String getName();

    /**
     * 触发器初始化
     * @throws TriggerException 触发器异常
     */
    void init() throws TriggerException;

    /**
     * 执行触发器
     * @param root 执行参数
     * @return 执行结果
     * @throws TriggerException 执行异常
     */
    TriggerResult execute(Map<String, Object> root) throws TriggerException;
}
