package org.webbuilder.utils.script;

import org.junit.Assert;
import org.junit.Test;
import org.webbuilder.utils.base.StringUtil;
import org.webbuilder.utils.base.file.CallBack;
import org.webbuilder.utils.base.file.FileUtil;
import org.webbuilder.utils.base.file.ReadCallBack;
import org.webbuilder.utils.script.engine.DynamicScriptEngine;
import org.webbuilder.utils.script.engine.DynamicScriptEngineFactory;
import org.webbuilder.utils.script.engine.ExecuteResult;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 浩 on 2015-10-27 0027.
 */
public class TestScript {

    /**
     * 测试执行js脚本
     */
    @Test
    public void testJs() throws Exception {
        DynamicScriptEngine engine = DynamicScriptEngineFactory.getEngine("js");
        engine.init("var Integer = ".concat(Integer.class.getName()).concat(";"));
        engine.compile("test", "return new Integer(1);");
        ExecuteResult result = engine.execute("test", new HashMap<String, Object>());
        Assert.assertEquals(result.getResult(), 1);
    }

    /**
     * 测试执行groovy
     */
    @Test
    public void testGrv() throws Exception {
        DynamicScriptEngine engine = DynamicScriptEngineFactory.getEngine("groovy");
        //engine.init("i=1;", "i = 20;");
        engine.compile("test", "return user;");
        ExecuteResult result = engine.execute("test", new HashMap<String, Object>() {
            {
                put("user", 20);
            }
        });
        Assert.assertEquals(result.getResult(), 20);
    }

    /**
     * 测试执行SpEL
     */
    @Test
    public void testSpel() throws Exception {
        DynamicScriptEngine engine = DynamicScriptEngineFactory.getEngine("spel");
        engine.compile("test", "#user[name]+'('+#user[age]+')'");
        Map<String, Object> root = new HashMap<>();
        root.put("user", new HashMap<Object, Object>() {{
            put("name", "张三");
            put("age", 10);
        }});
        ExecuteResult result = engine.execute("test", root);
        Assert.assertEquals(result.getResult(), "张三(10)");
    }

    /**
     * 测试执行ognl
     */
    @Test
    public void testOgnl() throws Exception {
        DynamicScriptEngine engine = DynamicScriptEngineFactory.getEngine("ognl");
        engine.compile("test", "user.name");
        Map<String, Object> root = new HashMap<>();
        root.put("user", new HashMap<Object, Object>() {{
            put("name", "张三");
        }});
        ExecuteResult result = engine.execute("test", root);
        Assert.assertEquals(result.getResult(), "张三");
    }

    /**
     * 测试执行clojure
     */
    @Test
    public void testClojure() throws Exception {
        FileUtil.readFile("d:/", true, new CallBack() {
            @Override
            public void isFile(File file) {
                System.out.println(file);
            }

            @Override
            public void isDir(File dir) {

            }

            @Override
            public void readError(File file, Throwable e) {

            }
        });

    }
}
