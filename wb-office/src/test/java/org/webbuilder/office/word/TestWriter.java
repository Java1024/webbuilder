package org.webbuilder.office.word;

import org.junit.Test;
import org.webbuilder.utils.base.Resources;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 浩 on 2015-12-18 0018.
 */
public class TestWriter {

    @Test
    public void testWriteTemplate() throws Exception {
        try (InputStream in = new FileInputStream(Resources.getResourceAsFile("docx/test.docx"));
             OutputStream out = new FileOutputStream("d:\\test.docx")) {
            //构造 模板所需的变量
            Map<String, Object> vars = new HashMap<>();
            vars.put("name", "姓名");
            vars.put("list", new ArrayList<Object>() {
                {
                    add(new HashMap<String, Object>() {
                        {
                            put("name", "张三");
                            put("sex", true);
                            put("age", 10);
                            put("remark", "测试");
                        }
                    });
                    add(new HashMap<String, Object>() {
                        {
                            put("name", "李四");
                            put("sex", false);
                            put("age", 10);
                            put("remark", "测试2");
                        }
                    });
                }
            });
            WordIO.writeTemplate(in, out, vars);
            out.flush();
        }
    }
}
