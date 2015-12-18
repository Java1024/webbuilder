package org.webbuilder.sql.trigger;

import org.webbuilder.sql.exception.TriggerException;
import org.webbuilder.utils.base.MD5;
import org.webbuilder.utils.script.engine.DynamicScriptEngine;
import org.webbuilder.utils.script.engine.DynamicScriptEngineFactory;
import org.webbuilder.utils.script.engine.ExecuteResult;

import java.io.Serializable;
import java.util.Map;

/**
 * 基于动态脚本引擎的触发器,执行的脚本需要有一个返回值
 * Created by 浩 on 2015-11-14 0014.
 */
public class ScriptTriggerSupport implements Trigger, Serializable {

    /**
     * 触发器名称
     */
    private String name;

    /**
     * 脚本ID
     */
    private String id;

    /**
     * 脚本语言,默认js,支持groovy,ognl,spel等
     */
    private String language = "js";

    /**
     * 脚本内容
     */
    private String content;

    /**
     * 脚本引擎
     */
    private DynamicScriptEngine engine;

    public ScriptTriggerSupport() {
    }

    /**
     * 带参数的构造方法
     *
     * @param id       脚本ID
     * @param name     触发器名称
     * @param language 脚本语言
     * @param content  脚本内容
     */
    public ScriptTriggerSupport(String id, String name, String language, String content) {
        this.id = id;
        this.name = name;
        this.language = language;
        this.content = content;
    }

    /**
     * 带参数的构造方法
     *
     * @param name     触发器名称
     * @param language 脚本语言
     * @param content  脚本内容
     */
    public ScriptTriggerSupport(String name, String language, String content) {
        this.name = name;
        this.language = language;
        this.content = content;
    }

    @Override
    public TriggerResult execute(Map<String, Object> root) throws TriggerException {
        //执行脚本
        ExecuteResult result = engine.execute(getId(), root);
        //解析执行结果
        TriggerResult triggerResult = new TriggerResult();
        if (result.isSuccess()) {
            Object res = result.getResult();
            if (res != null) {
                if (res instanceof Boolean) {
                    triggerResult.setSuccess(((Boolean) res));
                } else if (res instanceof String) {
                    triggerResult.setSuccess(false);
                    triggerResult.setMessage(res.toString());
                } else if (res instanceof Map) {
                    Map<String, Object> res_map = ((Map) res);
                    triggerResult.setSuccess(!"false".equals(String.valueOf(res_map.get("success"))));
                    triggerResult.setMessage(String.valueOf(res_map.get("message")));
                    triggerResult.setData(res_map.get("data"));
                } else if (res instanceof TriggerResult) {
                    triggerResult = ((TriggerResult) res);
                } else {
                    triggerResult.setSuccess(false);
                }
            }
        } else {
            triggerResult.setSuccess(false);
            triggerResult.setData(result.getResult());
            triggerResult.setMessage(result.getMessage());
        }
        return triggerResult;
    }

    @Override
    public void init() throws TriggerException {
        //获取动态脚本引擎
        engine = DynamicScriptEngineFactory.getEngine(getLanguage());
        if (engine == null) {
            throw new TriggerException(String.format("init trigger error ,cause by language %s not support", getLanguage()));
        }
        try {
            //编译脚本
            engine.compile(getId(), content);
        } catch (Exception e) {
            throw new TriggerException(String.format("init trigger error ,cause by %s", e.getMessage()), e);
        }
    }

    public String getId() {
        //未设置id则使用系统纳秒时间的MD5值作为ID
        if (id == null)
            id = MD5.encode(String.valueOf(System.nanoTime()));
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String getName() {
        return name;
    }

}
