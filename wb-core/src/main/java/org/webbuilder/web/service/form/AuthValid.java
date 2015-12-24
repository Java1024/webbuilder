package org.webbuilder.web.service.form;

import org.springframework.util.Assert;
import org.webbuilder.sql.FieldMetaData;
import org.webbuilder.sql.Table;
import org.webbuilder.sql.TableMetaData;
import org.webbuilder.utils.common.StringUtils;
import org.webbuilder.web.core.exception.AccessValidException;
import org.webbuilder.web.po.user.User;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by 浩 on 2015-12-23 0023.
 */
public class AuthValid {

    public static final boolean tryValidAuth(User user, int mode, String[] roles, String[] modules, String... levels) {
        if (modules == null && roles == null) return true;
        boolean access = false;
        //优先验证角色
        if (null != roles) {
            for (String role : roles) {
                access = user.hasAccessRole(role);
                //满足任意一个条件
                if (mode == 0 && access) {
                    return true;
                }
                //如果有一个不满足
                if (mode == 1 && !access) {
                    throw new AccessValidException("无访问权限");
                }
            }
        } else if (null != modules) {
            for (String module : modules) {
                access = user.hasAccessModuleLevel(module, levels);
                //满足任意一个条件
                if (mode == 0 && access) {
                    return true;
                }
                //如果有一个不满足
                if (mode == 1 && !access) {
                    throw new AccessValidException("无访问权限");
                    //return false;
                }
            }

        }
        if (!access) {
            throw new AccessValidException("无访问权限");
        }
        return true;
    }

    public static final boolean tryValidAuth(User user, Table table, String... levels) {
        Assert.notNull(user, "user不能为null");
        Assert.notNull(table, "table不能为null");
        TableMetaData metaData = table.getMetaData();
        int mode = StringUtils.toInt(metaData.attr("auth-mode"), 0);
        String[] modules = StringUtils.toStringAndSplit(metaData.attr("auth-module"), ",");
        String[] roles = StringUtils.toStringAndSplit(metaData.attr("auth-role"), ",");

        return tryValidAuth(user, mode, roles, modules, levels);
    }

    /**
     * 获取当前用户不能修改的字段
     *
     * @param user
     * @param table
     * @return
     */
    public static final Set<String> getCanNotUpdateFields(User user, Table table) {
        Assert.notNull(user, "user不能为null");
        Assert.notNull(table, "table不能为null");
        Set<String> fields = new LinkedHashSet<>();
        TableMetaData metaData = table.getMetaData();
        for (FieldMetaData fieldMetaData : metaData.getFields()) {
            String[] roles = StringUtils.toStringAndSplit(fieldMetaData.attr("auth-update-role"), ",");
            String[] modules = StringUtils.toStringAndSplit(fieldMetaData.attr("auth-update-module"), ",");
            int mode = StringUtils.toInt(fieldMetaData.attr("auth-mode"), 0);
            try {
                tryValidAuth(user, mode, roles, modules);
            } catch (AccessValidException e) {
                fields.add(fieldMetaData.getName());
            }

        }
        return fields;
    }

    /**
     * 获取当前用户不能修改的字段
     *
     * @param user
     * @param table
     * @return
     */
    public static final Set<String> getCanNotQueryFields(User user, Table table) {
        Assert.notNull(user, "user不能为null");
        Assert.notNull(table, "table不能为null");
        Set<String> fields = new LinkedHashSet<>();
        TableMetaData metaData = table.getMetaData();
        for (FieldMetaData fieldMetaData : metaData.getFields()) {
            String[] roles = StringUtils.toStringAndSplit(fieldMetaData.attr("auth-query-role"), ",");
            String[] modules = StringUtils.toStringAndSplit(fieldMetaData.attr("auth-query-module"), ",");
            int mode = StringUtils.toInt(fieldMetaData.attr("auth-mode"), 0);
            try {
                tryValidAuth(user, mode, roles, modules);
            } catch (AccessValidException e) {
                fields.add(fieldMetaData.getName());
            }
        }
        return fields;
    }
}
