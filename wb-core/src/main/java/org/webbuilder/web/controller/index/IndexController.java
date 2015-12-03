package org.webbuilder.web.controller.index;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.webbuilder.web.core.aop.logger.AccessLogger;
import org.webbuilder.web.core.authorize.annotation.Authorize;
import org.webbuilder.web.core.bean.ResponseMessage;
import org.webbuilder.web.core.utils.WebUtil;
import org.webbuilder.web.core.utils.http.session.HttpSessionManager;
import org.webbuilder.web.po.user.User;

import javax.annotation.Resource;

/**
 *
 * Created by 浩 on 2015-07-29 0029.
 */
@RestController
public class IndexController {

    @Resource
    private HttpSessionManager httpSessionManager;

    @RequestMapping(value = "/online/total", method = RequestMethod.GET)
    @AccessLogger("获取当前在线人数")
    public Object onlineTotal() {
        try {
            int size = httpSessionManager.getUserTotal();
            return new ResponseMessage(true, size);
        } catch (Exception e) {
            return new ResponseMessage(false, e);
        }
    }

    @RequestMapping(value = "/me/module", method = RequestMethod.GET)
    @AccessLogger("获取用户持有的权限")
    @Authorize
    public Object userRoles() {
        try {
            User user = WebUtil.getLoginUser();
            return user.getModules();
        } catch (Exception e) {
            return new ResponseMessage(false, e);
        }
    }

    @RequestMapping(value = "/online", method = RequestMethod.GET)
    @Authorize
    @AccessLogger("获取当前在线人员")
    public Object online() {
        try {
            return new ResponseMessage(true, httpSessionManager.getUserIdList());
        } catch (Exception e) {
            return new ResponseMessage(false, e);
        }
    }

}
