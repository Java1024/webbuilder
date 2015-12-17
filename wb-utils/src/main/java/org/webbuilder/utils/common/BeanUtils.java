package org.webbuilder.utils.common;

import org.webbuilder.utils.common.bean.ASMAttributeUtil;
import org.webbuilder.utils.common.bean.AttributeUtil;
import org.webbuilder.utils.common.bean.JDKAttributeUtil;


/**
 * Created by 浩 on 2015-12-09 0009.
 */
public class BeanUtils {

    private static final AttributeUtil jdk = new JDKAttributeUtil();

    private static final AttributeUtil asm = new ASMAttributeUtil();

    public static final <T> T attr(String attr, Object value) {
        try {
            return getJdkAttrUtil().attr(attr, value);
        } catch (Exception e) {
            return null;
        }
    }

    public static final void attr(Object object, String attr, Object value) throws Exception {
        getJdkAttrUtil().attr(object, attr, value);
    }

    public static AttributeUtil getJdkAttrUtil() {
        return jdk;
    }

    public static AttributeUtil getAsmAttrUtil() {
        return asm;
    }
}
