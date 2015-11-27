package org.webbuilder.web.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.webbuilder.web.core.utils.DynamicDeployBeans;
import org.webbuilder.web.service.user.UserService;

import javax.annotation.Resource;

/**
 * Created by 浩 on 2015-11-23 0023.
 */
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class DycBeanTest extends AbstractJUnit4SpringContextTests {

    @Resource
    private DynamicDeployBeans dynamicDeployBeans;

    @Resource
    private UserService userService;

    @Test
    public void testDeployBean() {
        System.out.println(userService.hashCode());
        //动态发布一个service
        dynamicDeployBeans.registerBean(UserService.class);

        System.out.println(userService.hashCode());
    }
}
