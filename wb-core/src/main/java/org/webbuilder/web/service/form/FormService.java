package org.webbuilder.web.service.form;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.webbuilder.sql.DataBase;
import org.webbuilder.sql.FieldMetaData;
import org.webbuilder.web.core.exception.BusinessException;
import org.webbuilder.web.core.service.GenericService;
import org.webbuilder.web.dao.form.FormMapper;
import org.webbuilder.web.po.form.Form;

import javax.annotation.Resource;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 自定义表单服务类
 * Created by generator
 *
 */
@Service
public class FormService extends GenericService<Form, String> {

    public static final Set<FieldMetaData> DEFAULT_FIELD = new LinkedHashSet<>();


    private static final String CACHE_KEY = "form";

    @Resource
    private DataBase dataBase;

    static {
        FieldMetaData u_id = new FieldMetaData("u_id", String.class, "varchar2(128)");
        u_id.setPrimaryKey(true);
        u_id.setComment("id主键");
        u_id.setAlias("主键");
        u_id.setCanUpdate(false);
        u_id.setLength(128);

        FieldMetaData creator_id = new FieldMetaData("creator_id", String.class, "varchar2(256)");
        creator_id.setPrimaryKey(false);
        creator_id.setComment("创建者id");
        creator_id.setAlias("创建者id");
        creator_id.setCanUpdate(false);
        creator_id.setLength(256);

        FieldMetaData create_date = new FieldMetaData("create_date", Date.class, "date");
        create_date.setNotNull(true);
        create_date.setCanUpdate(false);
        create_date.setComment("创建时间");

        FieldMetaData update_date = new FieldMetaData("update_date", Date.class, "date");
        update_date.setNotNull(false);
        update_date.setComment("最后一次修改时间");
        DEFAULT_FIELD.add(u_id);
        DEFAULT_FIELD.add(create_date);
        DEFAULT_FIELD.add(update_date);
        DEFAULT_FIELD.add(creator_id);
    }

    //默认数据映射接口
    @Resource
    protected FormMapper formMapper;

    @Override
    protected FormMapper getMapper() {
        return this.formMapper;
    }

    @Override
    @Cacheable(value = CACHE_KEY, key = "#id")
    public Form selectByPk(String id) throws Exception {
        return super.selectByPk(id);
    }

    @Override
    public int insert(Form data) throws Exception {
        Form old = this.selectByPk(data.getU_id());
        if (old != null)
            throw new BusinessException("该表单已存在!");
        saveTable(data);
        data.setCreate_date(new Date());
        return super.insert(data);
    }

    @Override
    @CacheEvict(value = CACHE_KEY, key = "#data.u_id")
    public int update(Form data) throws Exception {
        Form old = this.selectByPk(data.getU_id());
        if (old == null)
            throw new BusinessException("该表单不存在!");
        saveTable(data);
        data.setUpdate_date(new Date());
        return super.update(data);
    }

    protected void saveTable(Form data) throws Exception {
    }

    @Override
    public int delete(String s) throws Exception {
        throw new BusinessException("此服务已关闭!");
    }


}
