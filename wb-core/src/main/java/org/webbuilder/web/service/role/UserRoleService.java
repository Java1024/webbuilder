package org.webbuilder.web.service.role;

import org.webbuilder.web.core.service.GenericService;
import org.webbuilder.web.po.role.UserRole;
import org.webbuilder.web.dao.role.UserRoleMapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

/**
* 后台管理用户角色绑定服务类
* Created by generator 
*/
@Service
public class UserRoleService extends GenericService<UserRole,String> {

    //默认数据映射接口
    @Resource
    protected UserRoleMapper userRoleMapper;

    @Override
    protected UserRoleMapper getMapper(){
        return this.userRoleMapper;
    }

}
