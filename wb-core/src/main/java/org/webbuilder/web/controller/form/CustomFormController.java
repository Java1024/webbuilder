package org.webbuilder.web.controller.form;

import org.springframework.web.bind.annotation.*;
import org.webbuilder.web.core.aop.logger.AccessLogger;
import org.webbuilder.web.core.authorize.annotation.Authorize;
import org.webbuilder.web.core.bean.JsonParam;
import org.webbuilder.web.core.bean.PageUtil;
import org.webbuilder.web.core.bean.ResponseData;
import org.webbuilder.web.core.bean.ResponseMessage;
import org.webbuilder.web.service.form.CustomFormService;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 自定义表单数据访问接口
 * Created by 浩 on 2015-08-01 0001.
 */
@RestController
@RequestMapping(value = "/cf", produces = ResponseMessage.CONTENT_TYPE_JSON)
@AccessLogger("自定义表单")
public class CustomFormController {

    @Resource
    protected CustomFormService customFormService;

    @RequestMapping(value = "/{form_id}", method = RequestMethod.GET)
    @AccessLogger("查看列表")
    @Authorize(expression = "#user.hasAccessModuleLevel(#form_id,'R')")
    public Object list(@PathVariable("form_id") String form_id, @JsonParam PageUtil pageUtil) {
        // 获取条件查询
        try {
            Map<String, Object> data = customFormService.selectPager(form_id, pageUtil);
            return data;
        } catch (Exception e) {
            return new ResponseMessage(false, e);
        }
    }

    @RequestMapping(value = "/{form_id}/{id}", method = RequestMethod.GET)
    @AccessLogger("查看详情")
    @Authorize(expression = "#user.hasAccessModuleLevel(#form_id,'R')")
    public Object info(@PathVariable("form_id") String form_id, @PathVariable("id") String id) {
        try {
            Object data = customFormService.selectByPk(form_id, id);
            return new ResponseMessage(true, data);
        } catch (Exception e) {
            return new ResponseMessage(false, e);
        }
    }

    @RequestMapping(value = "/{form_id}/total", method = RequestMethod.GET)
    @AccessLogger("数量")
    @Authorize(expression = "#user.hasAccessModuleLevel(#form_id,'R')")
    public Object total(@PathVariable("form_id") String form_id, PageUtil pageUtil) {
        try {
            // 获取条件查询
            return new ResponseMessage(true, customFormService.total(form_id, pageUtil.params()));
        } catch (Exception e) {
            return new ResponseMessage(false, e);
        }
    }

    @RequestMapping(value = "/{form_id}", method = RequestMethod.POST)
    @AccessLogger("新增")
    @Authorize(expression = "#user.hasAccessModuleLevel(#form_id,'C')")
    public Object add(@PathVariable("form_id") String form_id,
                      @RequestBody Map<String, Object> data) {
        try {
            String id = customFormService.insert(form_id, data);
            return new ResponseMessage(true, id);
        } catch (Exception e) {
            return new ResponseMessage(false, e);
        }
    }

    @RequestMapping(value = "/{form_id}/{id}", method = RequestMethod.PUT)
    @AccessLogger("修改")
    @Authorize(expression = "#user.hasAccessModuleLevel(#form_id,'U')")
    public Object update(@PathVariable("form_id") String form_id,
                         @PathVariable("id") String id,
                         @RequestBody Map<String, Object> data) {
        try {
            int i = customFormService.update(form_id, id, data);
            return new ResponseMessage(true, i);
        } catch (Exception e) {
            return new ResponseMessage(false, e);
        }
    }

}
