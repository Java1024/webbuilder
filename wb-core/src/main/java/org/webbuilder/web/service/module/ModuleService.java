package org.webbuilder.web.service.module;

import org.webbuilder.web.core.exception.BusinessException;
import org.webbuilder.web.core.service.GenericService;
import org.webbuilder.web.po.module.Module;
import org.webbuilder.web.dao.module.ModuleMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统模块服务类
 * Created by generator
 *
 */
@Service
public class ModuleService extends GenericService<Module, String> {

    //默认数据映射接口
    @Resource
    protected ModuleMapper moduleMapper;

    @Override
    protected ModuleMapper getMapper() {
        return this.moduleMapper;
    }

    @Override
    public int update(List<Module> datas) throws Exception {
        int size = 0;
        for (Module module : datas) {
            tryValidPo(module);
            boolean doUpdate = (this.selectByPk(module.getOld_id()) != null);
            if (!module.getU_id().equals(module.getOld_id())) {
                if (doUpdate && this.selectByPk(module.getU_id()) != null) {
                    throw new BusinessException(String.format("标识:%s已存在", module.getU_id()));
                }
            }
            if (doUpdate) {
                size += this.update(module);
            } else {
                size += this.insert(module);
            }
        }
        return size;
    }

    public List<Module> selectByPid(String pid) throws Exception {
        return this.select(new QueryCondition().put("p_id", pid));
    }
}
