package org.webbuilder.office.excel;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webbuilder.office.excel.config.ExcelReaderCallBack;
import org.webbuilder.office.excel.support.CommonExcelReader;
import org.webbuilder.office.excel.wrapper.HashMapWrapper;
import org.webbuilder.utils.file.Resources;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 浩 on 2015-12-07 0007.
 */
public class TestReader {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 测试将excel表格转为map
     */
    @Test
    public void testRead2Map() throws Exception {
        try (InputStream in = Resources.getResourceAsStream("User.xlsx")) {
            List<Map<String, Object>> dataList = ExcelIO.read2Map(in);
            Assert.assertEquals(dataList.size(), 2);
            logger.info(dataList.toString());
        }
    }

    /**
     * 测试将excel表格转为bean
     */
    @Test
    public void testRead2Bean() throws Exception {
        try (InputStream in = Resources.getResourceAsStream("User.xlsx")) {
            //设置表头与字段映射,可通过反射获取
            Map<String, String> mapper = new HashMap<>();
            mapper.put("姓名", "name");
            mapper.put("年龄", "age");
            mapper.put("备注", "remark");
            //解析为User对象集合
            List<User> dataList = ExcelIO.read2Bean(in, mapper, User.class);
            Assert.assertEquals(dataList.size(), 2);
            logger.info(dataList.toString());
        }
    }


    /**
     * 自定义方式读取一个excel
     * @throws Exception
     */
    @Test
    public void testReadComplicated() throws Exception {
        try (InputStream in = Resources.getResourceAsStream("Test.xlsx")) {
            CommonExcelReader<Map<String, Object>> reader = new CommonExcelReader<Map<String, Object>>() {
                @Override
                protected boolean isHeader(ExcelReaderCallBack.CellContent content, List header) {
                    //第二行开始为表头
                    return content.getRow() == 1;
                }
            };
            //设置包装器
            reader.setWrapper(new HashMapWrapper());
            List<Map<String, Object>> list = reader.readExcel(in);

            for (Map<String, Object> map : list) {
                logger.info(map.toString());
            }
            Assert.assertEquals(list.size(), 33);
        }
    }

}
