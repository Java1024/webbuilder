package org.webbuilder.web.core.validator.form.suport;

import org.webbuilder.sql.validator.Validator;
import org.webbuilder.utils.common.BeanUtils;
import org.webbuilder.utils.common.ClassUtils;
import org.webbuilder.utils.script.engine.DynamicScriptEngine;
import org.webbuilder.utils.script.engine.DynamicScriptEngineFactory;
import org.webbuilder.web.core.bean.GenericPo;
import org.webbuilder.web.core.bean.ValidResults;

import javax.validation.ValidationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基于Groovy动态Bean对象验证
 * 原理，通过调用groovy脚本获取一个已编译的动态bean实例，然后进行赋值后验证
 * Created by 浩 on 2015-12-24 0024.
 */
public class GroovyDycBeanValidator implements Validator {

    protected static DynamicScriptEngine engine = DynamicScriptEngineFactory.getEngine("groovy");

    private String className;

    public GroovyDycBeanValidator(String className) {
        this.className = className;
    }

    public List valid(Object data,boolean insert) {
        ValidResults res = new ValidResults();
        if (!(data instanceof Map)) {
            res.addResult("error", "数据类型错误");
            return res;
        } else {
            try {
                Class<GenericPo> poClass = (Class<GenericPo>) engine.execute(className , new HashMap<String, Object>()).getResult();
                GenericPo po = poClass.newInstance();
                Map<String, Object> mapData = ((Map) data);
                for (Map.Entry<String, Object> entry : mapData.entrySet()) {
                    BeanUtils.attr(po, entry.getKey(), entry.getValue());
                }
                ValidResults tmp = po.valid();
                for (ValidResults.ValidResult re : tmp) {
                    if (insert) {
                        res.add(re);
                    } else if (mapData.containsKey(re.getField())) {
                        res.add(re);
                    }
                }
            } catch (ClassNotFoundException e) {
                res.addResult("error", "验证器配置错误:0x01");
            } catch (Exception e) {
                res.addResult("error", "验证器配置错误:0x03");
            }
        }
        //采用异常通知方式
        if (!res.isSuccess())
            throw new ValidationException(res.toString());
        return res;
    }

    @Override
    public List insertValid(Object data) {
        return valid(data,true);
    }

    @Override
    public List updateValid(Object data) {
        return valid(data,false);
    }
}
