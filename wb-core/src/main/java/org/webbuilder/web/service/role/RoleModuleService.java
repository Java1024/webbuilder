package org.webbuilder.web.service.role;

import org.webbuilder.web.core.service.GenericService;
import org.webbuilder.web.po.role.RoleModule;
import org.webbuilder.web.dao.role.RoleModuleMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 系统模块角色绑定服务类
 * Created by generator
 *
 */
@Service
public class RoleModuleService extends GenericService<RoleModule, String> {

    //默认数据映射接口
    @Resource
    protected RoleModuleMapper roleModuleMapper;

    @Override
    protected RoleModuleMapper getMapper() {
        return this.roleModuleMapper;
    }


    public boolean on(RoleModule roleModule, String event, Map<String, Object> param) {

        return true;
    }

}
