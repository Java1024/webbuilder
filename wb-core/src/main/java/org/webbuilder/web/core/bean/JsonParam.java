package org.webbuilder.web.core.bean;

import java.lang.annotation.*;

/**
 * springmvc 使用json作为参数绑定
 * Created by 浩 on 2015-09-29 0029.
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JsonParam {
    /**
     * 指定某个参数。如果不指定，则模糊匹配，将参数列表的所有键值进行填充，如，username=zs&name=张三。 username和name都会被识别填充到对象里
     */
    String value() default "";

    /**
     * 指定序列化的类型，如果参数类型是一个接口或者抽象类，则需要指定实现类，否则无法转换
     *
     * @return
     */
    Class<?> type() default Object.class;

    /**
     * 默认值，如果参数中对应的参数为null或者空字符，则使用默认值进行转换
     */
    String defaultValue() default "";
}
