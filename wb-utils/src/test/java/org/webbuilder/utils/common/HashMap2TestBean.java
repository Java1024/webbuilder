package org.webbuilder.utils.common;

import java.util.*;

import org.webbuilder.utils.script.engine.java.Executor;
import org.webbuilder.utils.common.ClassUtils;

public class HashMap2TestBean implements Executor {
    @Override
    public Object execute(Map<String, Object> var) throws Exception {
        java.util.HashMap source = (java.util.HashMap) var.get("source");
        org.webbuilder.utils.common.TestBean target = (org.webbuilder.utils.common.TestBean) var.get("target");
        target.setId(ClassUtils.cast(source.get("id"), int.class));
        target.setValid(ClassUtils.cast(source.get("valid"), boolean.class));
        target.setName(ClassUtils.cast(source.get("name"), java.lang.String.class));
        target.setIsTrue(ClassUtils.cast(source.get("isTrue"), boolean.class));
        target.setDate(ClassUtils.cast(source.get("date"), java.util.Date.class));
        return target;
    }

}