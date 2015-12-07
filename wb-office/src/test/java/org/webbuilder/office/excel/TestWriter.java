package org.webbuilder.office.excel;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.junit.Before;
import org.junit.Test;
import org.webbuilder.office.excel.config.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by 浩 on 2015-12-07 0007.
 */
public class TestWriter {

    private List<Header> headers = new LinkedList<>();

    private List<Object> datas = new ArrayList<>();

    @Before
    public void initData() {
        headers.add(new Header("年级", "grade"));
        headers.add(new Header("班级", "classes"));
        headers.add(new Header("性别", "sex"));
        headers.add(new Header("姓名", "name"));
        headers.add(new Header("年龄", "age"));
        headers.add(new Header("备注", "remark"));

        //创建模拟数据
        for (int i = 0; i < 2; i++) {
            final int t = i;
            datas.add(new HashMap<String, Object>() {
                {
                    put("grade", "一年级");
                    put("classes", "2班");
                    put("sex", "男");
                    put("name", "张三" + t);
                    put("age", t);
                    put("remark", "测试2");
                }
            });
        }
        for (int i = 0; i < 3; i++) {
            final int t = i;
            datas.add(new HashMap<String, Object>() {
                {
                    put("grade", "一年级");
                    put("classes", "2班");
                    put("sex", "女");
                    put("name", "张三" + t);
                    put("age", t);
                    put("remark", "测试2");
                }
            });
        }
        for (int i = 0; i < 3; i++) {
            final int t = i;
            datas.add(new HashMap<String, Object>() {
                {
                    put("grade", "一年级");
                    put("classes", "3班");
                    put("sex", "女");
                    put("name", "李四" + t);
                    put("age", t);
                    put("remark", "测试2");
                }
            });
        }

        for (int i = 0; i < 10; i++) {
            final int t = i;
            datas.add(new HashMap<String, Object>() {
                {
                    put("grade", "一年级");
                    put("classes", "3班");
                    put("sex", "女");
                    put("name", "李四__" + t);
                    put("age", t);
                    put("remark", "测试2");
                }
            });
        }

        for (int i = 0; i < 5; i++) {
            final int t = i;
            datas.add(new HashMap<String, Object>() {
                {
                    put("grade", "二年级");
                    put("classes", "3班");
                    put("sex", "男");
                    put("name", "李四__" + t);
                    put("age", t);
                    put("remark", "测试2");
                }
            });
        }


        for (int i = 0; i < 10; i++) {
            final int t = i;
            datas.add(new HashMap<String, Object>() {
                {
                    put("grade", "二年级");
                    put("classes", "1班");
                    put("name", "测试11" + t);
                    put("age", 12.3);
                    put("remark", "测试2");
                }
            });
        }
    }


    @Test
    public void testWrite() throws Exception {
        try (OutputStream outputStream = new FileOutputStream("C:\\Users\\浩\\Desktop\\test_1.xlsx")) {
            ExcelIO.write(outputStream, headers, datas);
            outputStream.flush();
        }
    }

    @Test
    public void testWriteCustom() throws Exception {
        try (OutputStream outputStream = new FileOutputStream("C:\\Users\\浩\\Desktop\\test_2.xlsx")) {
            ExcelWriterConfig config = new ExcelWriterConfig();
            //设置表头和数据
            config.setHeaders(headers);
            config.setDatas(datas);
            //1、自动合并年级和班级相同的列
            config.mergeColumn("grade", "classes", "sex");
            //2、从第2行开始写出
            config.setStartWith(1);
            //3、合并第一行的第一列到第四列,因为设置了startWith起始行号为1,所以第一列为-1
            config.addMerge(-1, 0, 5, -1);

            config.setCallBack(new CommonExcelWriterCallBack() {
                @Override
                public Object startBefore(int row, int column) {
                    //被跳过的行(代码[2、]处设置)填充此值
                    return "这是一个自动合并单元格并且自定义样式的示例";
                }

                @Override
                public CustomCellStyle getCellStyle(int row, int column, String header, Object value) {
                    CustomCellStyle style = super.getCellStyle(row, column, header, value);
                    //不为表头或者第一行的姓名列
                    if (row > 0 && "姓名".equals(header)) {
                        //设置红色
                        style.setFontColor(HSSFColor.RED.index);
                    } else {
                        style.setFontColor(HSSFColor.BLACK.index);
                    }
                    return style;
                }

                @Override
                public CustomRowStyle getRowStyle(int row, String header) {
                    if (row == -1) {
                        //表头高度
                        return new CustomRowStyle(20);
                    }
                    if (row == 0) {
                        //第一行的高度
                        return new CustomRowStyle(50);
                    }
                    return null;
                }

                @Override
                public CustomColumnStyle getColumnStyle(int column, String header) {
                    //设置姓名列的宽度
                    if ("姓名".equals(header)) {
                        return new CustomColumnStyle(5000);
                    }
                    return null;
                }
            });

            //第二个sheet
            ExcelWriterConfig config2 = new ExcelWriterConfig();
            config2.setSheetName("第二个");
            //设置表头和数据
            config2.setHeaders(headers);
            config2.setDatas(datas);
            //写出
            ExcelIO.write(outputStream, config, config2);
            outputStream.flush();
        }
    }
}
