package org.webbuilder.utils.common;


import org.webbuilder.utils.common.bean.imp.FastCopyUtil;
import org.webbuilder.utils.file.FileUtils;
import org.webbuilder.utils.file.callback.AbstractScanCallBack;
import org.webbuilder.utils.script.engine.DynamicScriptEngine;
import org.webbuilder.utils.script.engine.DynamicScriptEngineFactory;
import org.webbuilder.utils.script.engine.java.Executor;
import org.webbuilder.utils.script.engine.java.JavaEngine;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 浩 on 2015-12-14 0014.
 */
public class CommonTest {

    public static void main(String[] args) throws Exception {

        Map<String, Object> map = new HashMap<>();
        map.put("date", new Date());
        map.put("id", 10);
        map.put("name", "张三");
        map.put("valid", true);
        map.put("isTrue", true);
        FastCopyUtil copyUtil = new FastCopyUtil();
        TestBean bean = new TestBean();
        copyUtil.copy(map, bean);


        bean = new TestBean();
        long t = System.currentTimeMillis();
        bean = new TestBean();
        HashMap2TestBean hashMap2TestBean = new HashMap2TestBean();
        t = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            Map<String, Object> var = new HashMap<>();
            var.put("target", bean);
            var.put("source", map);
            hashMap2TestBean.execute(var);
        }
        System.out.println(System.currentTimeMillis() - t);
        System.out.println(bean);
        bean = new TestBean();
        t = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            copyUtil.copy(map, bean);
        }
        System.out.println(System.currentTimeMillis() - t);
        System.out.println(bean);

        System.out.println("exec");
        t = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            Map<String, Object> var = new HashMap<>();
            var.put("target", bean);
            var.put("source", map);
            JavaEngine.executorCache.get(copyUtil.getCache(map.getClass(),bean.getClass()).getFullClassName())
                    .execute(var);
        }
        System.out.println(System.currentTimeMillis() - t);
        System.out.println(bean);
        bean = new TestBean();

        System.out.println("exec2");
        t = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            Map<String, Object> var = new HashMap<>();
            var.put("target", bean);
            var.put("source", map);
            JavaEngine.executorCache.get("org.webbuilder.bean.copy.proxy.org.webbuilder.utils.common.HashMap2TestBean")
                    .execute(var);
        }
        System.out.println(System.currentTimeMillis() - t);
        System.out.println(bean);
        bean = new TestBean();

        System.out.println("exec3");
        DynamicScriptEngine java = DynamicScriptEngineFactory.getEngine("java");
        t = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            Map<String, Object> var = new HashMap<>();
            var.put("target", bean);
            var.put("source", map);
            java.execute("org.webbuilder.bean.copy.proxy.org.webbuilder.utils.common.HashMap2TestBean", var);
        }
        System.out.println(System.currentTimeMillis() - t);
        System.out.println(bean);
        bean = new TestBean();
        t = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            BeanUtils.copy(map, bean);
        }
        System.out.println(System.currentTimeMillis() - t);
        System.out.println(bean);

    }

    private static final Map<String, Method> cache = new ConcurrentHashMap<>();

    public static Map<String, Object> transBean2Map(Object obj) {
        if (obj == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<>();
        try {
            if (cache.size() == 0) {
                BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
                PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
                for (PropertyDescriptor property : propertyDescriptors) {
                    String key = property.getName();
                    // 过滤class属性
                    if (!key.equals("class")) {
                        // 得到property对应的getter方法
                        Method getter = property.getReadMethod();
                        if (getter == null) {
                            continue;
                        }
                        cache.put(key, getter);
                    }
                }
            }
            for (Map.Entry<String, Method> entry : cache.entrySet()) {
                Object value = entry.getValue().invoke(obj);
                map.put(entry.getKey(), value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
}
