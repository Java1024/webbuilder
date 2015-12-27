package org.webbuilder.web.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.webbuilder.web.core.utils.DynamicDeployBeans;
import org.webbuilder.web.service.basic.sql.SqlExecutor;
import org.webbuilder.web.service.user.UserService;

import javax.annotation.Resource;

/**
 * Created by 浩 on 2015-11-23 0023.
 */
@ContextConfiguration(locations = "classpath:applicationContext.xml")
@WebAppConfiguration
@TransactionConfiguration(defaultRollback = true)
public class DycBeanTest extends AbstractJUnit4SpringContextTests {

    @Resource
    private WebApplicationContext wac;

    private MockMvc mockMvc;

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
