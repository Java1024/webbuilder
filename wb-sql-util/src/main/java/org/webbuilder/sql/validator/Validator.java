package org.webbuilder.sql.validator;


import java.util.List;

/**
 * 表单数据验证器,验证传入的数据,是否符合验证条件
 * Created by 浩 on 2015-12-24 0024.
 */
public interface Validator {
    /**
     * 验证一个数据是否符合要求
     *
     * @param data 要验证的数据
     * @return 验证结果
     */
    List valid(Object data);
}
