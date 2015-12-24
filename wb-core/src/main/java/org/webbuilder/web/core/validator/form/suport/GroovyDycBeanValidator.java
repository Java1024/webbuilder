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
 * Created by 浩 on 2015-12-24 0024.
 */
public class GroovyDycBeanValidator implements Validator {

    protected static DynamicScriptEngine engine = DynamicScriptEngineFactory.getEngine("groovy");

    private String className;

    public GroovyDycBeanValidator(String className) {
        this.className = className;
    }

    @Override
    public List valid(Object data) {
        ValidResults res = new ValidResults();
        if (!(data instanceof Map)) {
            res.addResult("error", "数据类型错误");
            return res;
        } else {
            try {
                GenericPo po = (GenericPo) engine.execute(className + ".getInstance", new HashMap<String, Object>()).getResult();
                Map<String, Object> mapData = ((Map) data);
                for (Map.Entry<String, Object> entry : mapData.entrySet()) {
                    BeanUtils.attr(po, entry.getKey(), entry.getValue());
                }
                ValidResults tmp = po.valid();
                for (ValidResults.ValidResult re : tmp) {
                    if (mapData.containsKey(re.getField())) {
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

}
