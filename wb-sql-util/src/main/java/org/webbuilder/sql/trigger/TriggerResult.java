package org.webbuilder.sql.trigger;

import java.io.Serializable;

/**
 * 触发器执行结果
 * Created by 浩 on 2015-11-14 0014.
 */
public class TriggerResult implements Serializable {
    /**
     * 执行是否成功
     */
    private boolean success;

    /**
     * 返回的消息
     */
    private String message;

    /**
     * 返回的数据
     */
    private Object data;

    public TriggerResult() {
    }

    public TriggerResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public TriggerResult(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
