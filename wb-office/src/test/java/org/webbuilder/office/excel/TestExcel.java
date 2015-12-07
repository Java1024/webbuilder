package org.webbuilder.office.excel;

import org.junit.Test;
import org.webbuilder.office.excel.api.POIExcelApi;
import org.webbuilder.office.excel.config.AbstractExcelReaderCallBack;
import org.webbuilder.office.excel.config.ExcelReaderCallBack;
import org.webbuilder.office.excel.support.AbstractExcelReader;
import org.webbuilder.office.excel.support.CommonExcelReader;
import org.webbuilder.office.excel.wrapper.HashMapWrapper;
import org.webbuilder.utils.base.Resources;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 浩 on 2015-12-07 0007.
 */
public class TestExcel {

    @Test
    public void testMap() {
        try (InputStream in = Resources.getResourceAsStream("User.xlsx")) {
            List<Map<String, Object>> dataList = ExcelIO.read2Map(in);
            System.out.println(dataList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testBean() {
        try (InputStream in = Resources.getResourceAsStream("User.xlsx")) {
            //设置表头与字段映射,可通过反射获取
            Map<String, String> mapper = new HashMap<>();
            mapper.put("姓名", "name");
            mapper.put("年龄", "age");
            mapper.put("备注", "remark");

            //解析为User对象集合
            List<User> dataList = ExcelIO.read2Bean(in, mapper, User.class);
            System.out.println(dataList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
