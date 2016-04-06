package org.webbuilder.utils.common.bean.imp;

import org.webbuilder.utils.common.ClassUtils;
import org.webbuilder.utils.common.bean.BeanCopyUtil;
import org.webbuilder.utils.script.engine.DynamicScriptEngine;
import org.webbuilder.utils.script.engine.DynamicScriptEngineFactory;
import org.webbuilder.utils.script.engine.ExecuteResult;
import org.webbuilder.utils.script.engine.java.JavaEngine;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 浩 on 2016-01-25 0025.
 */
public class FastCopyUtil implements BeanCopyUtil {
    private DynamicScriptEngine engine = DynamicScriptEngineFactory.getEngine("java");
    private String packageName = "org.webbuilder.bean.copy.proxy";

    private Map<String, DycCache> cache = new HashMap<>();

    public static class DycCache {
        private String packageName;
        private String className;
        private String fullClassName;

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getFullClassName() {
            return fullClassName;
        }

        public void setFullClassName(String fullClassName) {
            this.fullClassName = fullClassName;
        }
    }

    public DycCache getCache(Class sourceClass, Class targetClass) {
        String cacheKey = sourceClass.hashCode()+""+targetClass.hashCode();
        DycCache dycCache = cache.get(cacheKey);
        if (dycCache == null) {
            dycCache = new DycCache();
            String packageName = this.packageName.concat(".") + targetClass.getPackage().getName();
            String className = sourceClass.getSimpleName().concat("2").concat(targetClass.getSimpleName());
            String fullClassName = packageName.concat(".").concat(className);
            dycCache.setPackageName(packageName);
            dycCache.setClassName(className);
            dycCache.setFullClassName(fullClassName);
            cache.put(cacheKey, dycCache);
        }

        return dycCache;
    }

    protected String tryBuildCopyCode(Class sourceClass, Class targetClass) throws Exception {
        DycCache cache = getCache(sourceClass, targetClass);
        String packageName = cache.getPackageName();
        String className = cache.getClassName();
        String fullClassName = cache.getFullClassName();
        //已编译则退出
        if (engine.compiled(fullClassName)) return fullClassName;

        StringBuilder code = new StringBuilder();
        code.append("package ").append(packageName).append(";\n");
        code.append("import java.util.*;\n");
        code.append("import org.webbuilder.utils.script.engine.java.Executor;\n");
        code.append("import org.webbuilder.utils.common.ClassUtils;\n");
        code.append("public class ").append(className).append(" implements Executor{\n");
        code.append("\t@Override\n").append(
                "\tpublic Object execute(Map<String, Object> var) throws Exception {\n")
                .append(buildCopyCode(sourceClass, targetClass))
                .append("\t\treturn target;\n")
                .append("\t}");

        code.append("\n}");
        engine.compile(fullClassName, code.toString());
        return fullClassName;
    }


    protected String buildCopyCode(Class sourceClass, Class targetClass) {
        StringBuilder code = new StringBuilder();
        code.append("\t\t").append(sourceClass.getName())
                .append(" source=(").append(sourceClass.getName()).append(")var.get(\"source\");\n");
        code.append("\t\t").append(targetClass.getName())
                .append(" target=(").append(targetClass.getName()).append(")var.get(\"target\");\n");

        //复制目标是map
        if (ClassUtils.instanceOf(targetClass, Map.class)) {
            CommonAttributeUtil.CacheInfo cacheInfo = CommonAttributeUtil.getInstance().getPropertyFromCache(sourceClass);
            for (Map.Entry<String, Method> methodEntry : cacheInfo.getGetter().entrySet()) {
                if (methodEntry.getValue() == null) continue;
                code.append("\t\t\ttarget.put(").append("\"").append(methodEntry.getKey())
                        .append("\",").append("source.").append(methodEntry.getValue().getName()).append("());\n");

            }
        }
        //复制源是map
        else if (ClassUtils.instanceOf(sourceClass, Map.class)) {
            CommonAttributeUtil.CacheInfo cacheInfo = CommonAttributeUtil.getInstance().getPropertyFromCache(targetClass);
            for (Map.Entry<String, Method> methodEntry : cacheInfo.getSetter().entrySet()) {
                if (methodEntry.getValue() == null) continue;
                code.append("\t\t\ttarget.").append(methodEntry.getValue().getName())
                        .append("(ClassUtils.cast(").append("source.get(\"").append(methodEntry.getKey())
                        .append("\"),").append(methodEntry.getValue().getParameterTypes()[0].getName())
                        .append(".class));\n");

            }
        } else {
            CommonAttributeUtil.CacheInfo sourceCache = CommonAttributeUtil.getInstance().getPropertyFromCache(sourceClass);
            CommonAttributeUtil.CacheInfo targetCache = CommonAttributeUtil.getInstance().getPropertyFromCache(targetClass);
            Map<String, Method> sourceGetter = sourceCache.getGetter();
            for (Map.Entry<String, Method> methodEntry : targetCache.getSetter().entrySet()) {
                Method targetGetter;
                if ((targetGetter = sourceGetter.get(methodEntry.getKey())) != null) {
                    if (methodEntry.getValue() == null) continue;
                    code.append("\t\t\ttarget.").append(methodEntry.getValue().getName())
                            .append("(ClassUtils.cast(").append("source.").append(targetGetter.getName())
                            .append("(),").append(methodEntry.getValue().getParameterTypes()[0].getName())
                            .append(".class));\n");
                }
            }
        }
        return code.toString();
    }


    @Override
    public <T> T copy(Object source, T target) throws Exception {
        String codeId = tryBuildCopyCode(source.getClass(), target.getClass());
        Map<String, Object> var = new HashMap<>();
        var.put("source", source);
        var.put("target", target);
        JavaEngine.executorCache.get(codeId).execute(var);
        return target;
    }

    @Override
    public <T> T copy(Object source, T target, boolean skipNull) throws Exception {
        return null;
    }

    @Override
    public <T> T deepCopy(Object source, T target) throws Exception {
        return null;
    }

    @Override
    public <T> T deepCopy(Object source, T target, boolean skipNull) throws Exception {
        return null;
    }

}
