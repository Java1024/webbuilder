package org.webbuilder.utils.common.bean;

/**
 * 对象属性操作工具
 * Created by 浩 on 2015-12-09 0009.
 */
public interface AttributeUtil {
    /**
     * 给对象填充指定的属性值,支持属性嵌套
     * 如:user.name。将设置user属性的name属性值,如果user属性值为null,将自动被初始化.
     * 注意:自动初始化仅支持包含无参数构造方法的非抽象类以及map
     *
     * @param object 要进行填充属性值的对象
     * @param attr   属性名
     * @param value  值
     * @throws Exception 填充异常
     */
    void attr(Object object, String attr, Object value) throws Exception;

    /**
     * 从指定的对象中获取一个属性值
     *
     * @param attr   属性名
     * @param object 目标对象
     * @param <T>    属性值类型泛型
     * @return 属性值, 未获取到时返回null
     * @throws Exception 获取属性异常
     */
    <T> T attr(String attr, Object object) throws Exception;
}
