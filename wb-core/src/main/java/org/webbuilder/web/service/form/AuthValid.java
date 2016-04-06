package org.webbuilder.web.service.form;

import org.springframework.util.Assert;
import org.webbuilder.sql.FieldMetaData;
import org.webbuilder.sql.Table;
import org.webbuilder.sql.TableMetaData;
import org.webbuilder.utils.common.StringUtils;
import org.webbuilder.web.core.exception.AccessValidException;
import org.webbuilder.web.po.user.User;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 权限验证器，用于动态表单中相关的权限验证
 * Created by 浩 on 2015-12-23 0023.
 */
public class AuthValid {

    /**
     * 验证模式
     */
    public enum ValidMode {
        /**
         * OR，当满足一个条件是即为true
         */
        or,
        /**
         * AND，当所有条件满足时才为true
         */
        and;

        /**
         * 将一个对象转为验证模式枚举，如果参数不为and或者AND,将返回or
         *
         * @param obj 要转换的对象
         * @return 转换后的结果
         */
        public static ValidMode parse(Object obj) {
            if (StringUtils.isNullOrEmpty(obj)) return or;
            if (obj.toString().trim().toLowerCase().equals("and")) return and;
            else return or;
        }
    }

    /**
     * 尝试验证一个用户是否拥有指定的权限。
     * 验证逻辑如下:
     * <ul>
     * <li>优先验证角色,如果指定了多个角色,角色支持*,和!匹配，如,[*,!guests],代表除了guests角色，其他角色都有权限</li>
     * </ul>
     *
     * @param user    用户对象
     * @param mode    验证模式 {@link ValidMode}
     * @param roles   角色列表
     * @param modules 模块列表
     * @param levels  模块访问级别
     * @return 是否拥有权限
     */
    public static final boolean tryValidAuth(User user, ValidMode mode, String[] roles, String[] modules, String... levels) {
        if (modules == null && roles == null) return true;
        boolean access = false;
        //优先验证角色
        if (null != roles && roles.length > 0) {
            boolean all = Arrays.asList(roles).contains("*");
            boolean notMyRole = false;
            for (String role : roles) {
                if ("*".equals(role)) continue;
                if (role.startsWith("!")) {
                    role = role.substring(1);
                    access = user.hasAccessRole(role);
                    //如果持有了不能访问的角色
                    if (access) {
                        access = false;
                        notMyRole = true;
                    }
                } else {
                    access = user.hasAccessRole(role);
                }
                //满足任意一个条件
                if (mode == ValidMode.or && access) {
                    access = true;
                    break;
                }
                //如果有一个不满足
                else if (mode == ValidMode.and && !access) {
                    access = false;
                    break;
                }
            }
            //如果配置了(*),且当前角色没有命中(!),则代表所有角色通过
            //如:  *,!test  代表除了test角色，其他角色都有此权限
            if (!access && !notMyRole && all) {
                access = true;
            }
        }
        //如果角色未通过验证，则开始验证模块
        else if (null != modules) {
            for (String module : modules) {
                access = user.hasAccessModuleLevel(module, levels);
                //满足任意一个条件
                if (mode == ValidMode.or && access) {
                    access = true;
                    break;
                }
                //如果有一个不满足
                else if (mode == ValidMode.and && !access) {
                    access = false;
                    break;
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
        String[] modules = StringUtils.toStringAndSplit(metaData.attr("auth-module"), ",");
        String[] roles = StringUtils.toStringAndSplit(metaData.attr("auth-role"), ",");
        Object mode = metaData.attr("auth-mode");
        return tryValidAuth(user, ValidMode.parse(mode), roles, modules, levels);
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
            Object mode = metaData.attr("auth-mode");
            try {
                tryValidAuth(user, ValidMode.parse(mode), roles, modules);
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
            Object mode = metaData.attr("auth-mode");
            try {
                tryValidAuth(user, ValidMode.parse(mode), roles, modules);
            } catch (AccessValidException e) {
                fields.add(fieldMetaData.getName());
            }
        }
        return fields;
    }
}
