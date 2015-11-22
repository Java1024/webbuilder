package org.webbuilder.web.service.user;

import org.springframework.stereotype.Service;
import org.webbuilder.web.core.service.GenericService;
import org.webbuilder.web.dao.user.UserMapper;
import org.webbuilder.web.po.module.Module;
import org.webbuilder.web.po.user.User;
import org.webbuilder.web.service.module.ModuleService;
import org.webbuilder.web.service.storage.StorageService;

import javax.annotation.Resource;
import java.util.*;

/**
 * 后台管理用户服务类
 * Created by generator
 *
 * @Copyright 2015 www.cqtaihong.com Inc. All rights reserved.
 * 注意：本内容仅限于重庆泰虹医药网络发展有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service
public class UserService extends GenericService<User, String> {

    //默认数据映射接口
    @Resource
    protected UserMapper userMapper;

    @Resource
    protected ModuleService moduleService;

    @Override
    protected UserMapper getMapper() {
        return this.userMapper;
    }

    @Resource
    protected StorageService storageService;

    public User selectByUserName(String username) throws Exception {
        return this.getMapper().selectByUserName(username);
    }

    public void initAdminUser(User user) throws Exception {
        HashMap map = new HashMap<>();
        map.put("sortField", "sort_index");
        map.put("sortOrder", "asc");
        List<Module> modules = moduleService.select(map);
        Map<Module, Set<String>> roleInfo = new LinkedHashMap<>();
        for (Module module : modules) {
            roleInfo.put(module, new LinkedHashSet<>(module.getM_optionMap().keySet()));
        }
        user.setRoleInfo(roleInfo);
    }

}
