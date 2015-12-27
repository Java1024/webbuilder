package org.webbuilder.web.service.form;

import org.springframework.stereotype.Service;
import org.webbuilder.sql.DataBase;
import org.webbuilder.sql.Query;
import org.webbuilder.sql.Table;
import org.webbuilder.sql.Update;
import org.webbuilder.sql.param.insert.InsertParam;
import org.webbuilder.sql.param.query.QueryParam;
import org.webbuilder.sql.param.update.UpdateParam;
import org.webbuilder.utils.common.StringUtils;
import org.webbuilder.web.core.bean.GenericPo;
import org.webbuilder.web.core.bean.PageUtil;
import org.webbuilder.web.core.dao.GenericMapper;
import org.webbuilder.web.core.exception.BusinessException;
import org.webbuilder.web.core.service.GenericService;
import org.webbuilder.web.core.utils.WebUtil;
import org.webbuilder.web.dao.form.FormMapper;
import org.webbuilder.web.po.form.CustomFormData;
import org.webbuilder.web.po.form.Form;
import org.webbuilder.web.po.user.User;
import org.webbuilder.web.service.storage.StorageService;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by 浩 on 2015-08-01 0001.
 */
@Service
public class CustomFormService extends GenericService<CustomFormData, String> {


    @Resource
    protected DataBase dataBase;

    @Resource
    protected FormMapper formMapper;

    @Resource
    protected FormService formService;

    /**
     * 获取默认的数据映射接口
     *
     * @return 数据映射接口
     */
    @Override
    protected <T extends GenericMapper<CustomFormData, String>> T getMapper() {
        return null;
    }

    @Resource
    protected StorageService storageService;

    public Form getForm(String id) throws Exception {
        Form form = formService.selectByPk(id);
        if (form == null) {
            throw new BusinessException(id + "表单不存在!");
        }
        return form;
    }

    public String insert(String form_id, Map<String, Object> data) throws Exception {
        Table table = getTable(form_id);
        AuthValid.tryValidAuth(WebUtil.getLoginUser(), table, "C");
        String idField = getIdFiled(table);
        String uid = (String) data.get(idField);
        if (StringUtils.isNullOrEmpty(uid)) {
            uid = GenericPo.createUID();
            data.put(idField, uid);
        }
        InsertParam param = new InsertParam();
        param.values(data);
        table.createInsert().insert(param);
        return String.valueOf(param.getData().get(idField));
    }

    public int update(String form_id, String u_id, Map<String, Object> data) throws Exception {
        Table table = getTable(form_id);
        User user = WebUtil.getLoginUser();
        AuthValid.tryValidAuth(user, table, "U");
        //获取不能当前用户不能进行修改的字段
        Set<String> cannotUpdate = AuthValid.getCanNotUpdateFields(user, table);
        for (String s : cannotUpdate) {
            data.remove(s);
        }
        Update update = table.createUpdate();
        UpdateParam param = new UpdateParam();
        param.where(getIdFiled(table), u_id);
        param.exclude(cannotUpdate);
        param.set(data);
        return update.update(param);
    }

    public Map selectByPk(String form_id, String u_id) throws Exception {
        Table table = getTable(form_id);
        User user = WebUtil.getLoginUser();
        //尝试验证查询权限
        AuthValid.tryValidAuth(user, table, "R");
        //获取不能查询的字段
        Set<String> canNotQuery = AuthValid.getCanNotQueryFields(user, table);
        QueryParam param = new QueryParam();
        param.where(getIdFiled(table), u_id);
        param.exclude(canNotQuery);
        return table.createQuery().single(param);
    }

    protected String getIdFiled(Table table) {
        Object idField = table.getMetaData().attr("id-field");
        String idF = StringUtils.isNullOrEmpty(idField) ? "u_id" : String.valueOf(idField);
        return idF;
    }

    /**
     * 根据主键查询并指定查询字段和排除字段
     *
     * @param form_id  表单id
     * @param u_id     主键
     * @param includes 要进行查询的字段
     * @param excludes 不查询的字段
     * @return 查询结果
     * @throws Exception
     */
    public Map selectByPk(String form_id, String u_id, List<String> includes, List<String> excludes) throws Exception {
        if (includes.size() == 0 && excludes.size() == 0)
            return this.selectByPk(form_id, u_id);
        Table table = getTable(form_id);
        User user = WebUtil.getLoginUser();
        AuthValid.tryValidAuth(user, table, "R");
        //获取用户不能查询的字段
        Set<String> canNotQuery = AuthValid.getCanNotQueryFields(user, table);
        QueryParam param = new QueryParam();
        param.where(getIdFiled(table), u_id);
        param.include(includes).exclude(excludes);
        param.exclude(canNotQuery);
        return table.createQuery().single(param);
    }

    public List<Map> select(String form_id, Map<String, Object> params) throws Exception {
        Table table = getTable(form_id);
        User user = WebUtil.getLoginUser();
        AuthValid.tryValidAuth(user, table, "R");
        //获取用户不能查询的字段
        Set<String> canNotQuery = AuthValid.getCanNotQueryFields(user, table);
        QueryParam param = new QueryParam();
        param.where(params);
        //排除不查询的字段
        param.exclude(canNotQuery);
        return table.createQuery().list(param);
    }

    @Override
    @Deprecated
    public int total(Map<String, Object> conditions) throws Exception {
        throw new BusinessException("此服务已关闭访问!");
    }

    protected Table getTable(String form_id) throws Exception {
        Table table = dataBase.getTable(form_id);
        if (table == null) {
            Form form = getForm(form_id);
            table = dataBase.getTable(form.getTable_name());
        }
        if (table == null)
            throw new BusinessException("未找到此表单！");
        if ("true".equals(table.getMetaData().attr("system"))) {
            throw new BusinessException("无法对此表单进行操作！");
        }
        return table;
    }

    public int total(String form_id, Map<String, Object> params) throws Exception {
        Table table = getTable(form_id);
        return table.createQuery().total(new QueryParam().where(params));
    }

    public Map<String, Object> selectPager(String form_id, PageUtil pageUtil) throws Exception {
        Table table = getTable(form_id);
        User user = WebUtil.getLoginUser();
        AuthValid.tryValidAuth(user, table, "R");
        Query query = table.createQuery();
        QueryParam param = new QueryParam();
        param.where(pageUtil.getQueryMap());
        int total = query.total(param);
        int pageIndex = pageUtil.pageIndex(total);
        param.doPaging(pageIndex, pageUtil.getPageSize());
        param.include(pageUtil.getIncludes());
        param.exclude(pageUtil.getExcludes());
        if (pageUtil.getSortField() != null) {
            param.orderBy("desc".equalsIgnoreCase(pageUtil.getSortOrder()), pageUtil.getSortField());
        }
        Set<String> canNotQuery = AuthValid.getCanNotQueryFields(user, table);
        param.exclude(canNotQuery);
        Map<String, Object> result = new HashMap<>();
        result.put("data", query.list(param));
        result.put("total", total);
        return result;
    }


    @Override
    @Deprecated
    public int insert(CustomFormData data) throws Exception {
        throw new BusinessException("此服务已关闭访问!");
    }

    @Override
    @Deprecated
    public int update(CustomFormData data) throws Exception {
        throw new BusinessException("此服务已关闭访问!");
    }

    @Override
    @Deprecated
    public CustomFormData selectByPk(String pk) throws Exception {
        throw new BusinessException("此服务已关闭访问!");
    }

    @Override
    @Deprecated
    public List<CustomFormData> select(Map<String, Object> conditions) throws Exception {
        throw new BusinessException("此服务已关闭访问!");
    }

    @Override
    @Deprecated
    public Map<String, Object> selectPager(PageUtil pageUtil) throws Exception {
        throw new BusinessException("此服务已关闭访问!");
    }


}
