package org.webbuilder.web.service.user;

import org.springframework.stereotype.Service;
import org.webbuilder.utils.common.MD5;
import org.webbuilder.web.core.exception.BusinessException;
import org.webbuilder.web.core.service.GenericService;
import org.webbuilder.web.core.utils.RandomUtil;
import org.webbuilder.web.dao.role.UserRoleMapper;
import org.webbuilder.web.dao.user.UserMapper;
import org.webbuilder.web.po.module.Module;
import org.webbuilder.web.po.role.UserRole;
import org.webbuilder.web.po.user.User;
import org.webbuilder.web.service.module.ModuleService;
import org.webbuilder.web.service.storage.StorageService;

import javax.annotation.Resource;
import java.util.*;

/**
 * 后台管理用户服务类
 * Created by generator
 */
@Service
public class UserService extends GenericService<User, String> {

    //默认数据映射接口
    @Resource
    protected UserMapper userMapper;

    @Resource
    protected UserRoleMapper userRoleMapper;


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

    @Override
    public int insert(User data) throws Exception {
        tryValidPo(data);
        if (selectByUserName(data.getUsername()) != null) {
            throw new BusinessException("用户名已存在!");
        }
        data.setU_id(RandomUtil.randomChar(6));
        data.setCreate_date(new Date());
        data.setUpdate_date(new Date());
        data.setPassword(MD5.encode(data.getPassword()));
        int i = userMapper.insert(data);
        if (data.getUserRoles().size() != 0) {
            for (UserRole userRole : data.getUserRoles()) {
                userRole.setU_id(RandomUtil.randomChar());
                userRole.setUser_id(data.getU_id());
                userRoleMapper.insert(userRole);
            }
        }
        return i;
    }

    @Override
    public int update(User data) throws Exception {
        tryValidPo(data);
        User old = this.selectByUserName(data.getUsername());
        if (old != null && !old.getU_id().equals(data.getU_id())) {
            throw new BusinessException("用户名已存在!");
        }
        data.setUpdate_date(new Date());
        if (!"$default".equals(data.getPassword())) {
            data.setPassword(MD5.encode(data.getPassword()));
            userMapper.updatePassword(data);
        }
        int i = userMapper.update(data);
        if (data.getUserRoles().size() != 0) {
            //删除所有
            userRoleMapper.deleteByUserId(data.getU_id());
            for (UserRole userRole : data.getUserRoles()) {
                userRole.setU_id(RandomUtil.randomChar());
                userRole.setUser_id(data.getU_id());
                userRoleMapper.insert(userRole);
            }
        }
        return i;
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
