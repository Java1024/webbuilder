package org.webbuilder.office.word;

import org.junit.Test;
import org.webbuilder.office.word.api.poi.POIWordApi4Docx;
import org.webbuilder.office.word.support.template.DOCXTemplateReader;
import org.webbuilder.utils.common.BeanUtils;
import org.webbuilder.utils.common.StringUtils;
import org.webbuilder.utils.file.Resources;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static void main(String[] args) throws Exception {
        try (FileInputStream template = new FileInputStream("/home/zhouhao/桌面/template.docx");
             FileInputStream source = new FileInputStream("/home/zhouhao/桌面/test.docx")) {
            List<Map<String,Object>> datas = new DOCXTemplateReader(template, source).read();
            for (Map<String, Object> data : datas) {
                System.out.println(data);
            }
        }
    }


}
