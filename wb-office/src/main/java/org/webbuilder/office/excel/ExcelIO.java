package org.webbuilder.office.excel;

import org.webbuilder.office.excel.config.ExcelWriterConfig;
import org.webbuilder.office.excel.config.Header;
import org.webbuilder.office.excel.support.CommonExcelReader;
import org.webbuilder.office.excel.support.CommonExcelWriter;
import org.webbuilder.office.excel.wrapper.BeanWrapper;
import org.webbuilder.office.excel.wrapper.HashMapWrapper;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * Excel读写操作类
 * Created by 浩 on 2015-12-07 0007.
 */
public class ExcelIO {

    /**
     * 读取excel为map集合
     *
     * @param inputStream excel输入流
     * @return map对象集合
     * @throws Exception 读取异常
     */
    public static List<Map<String, Object>> read2Map(InputStream inputStream) throws Exception {
        return read(inputStream, new HashMapWrapper());
    }

    /**
     * 读取excel为javaBean
     *
     * @param inputStream  excel输入流
     * @param headerMapper 表头与字段映射配置
     * @param <T>          bean泛型
     * @param tClass       javaBean类型
     * @return bean集合
     * @throws Exception 读取异常
     */
    public static <T> List<T> read2Bean(InputStream inputStream, Map<String, String> headerMapper, Class<T> tClass) throws Exception {
        BeanWrapper wrapper = new BeanWrapper<T>();
        wrapper.setType(tClass);
        wrapper.setHeaderNameMapper(headerMapper);
        return read(inputStream, wrapper);
    }

    /**
     * 自定义包装器读取excel为集合
     *
     * @param inputStream excel输入流
     * @param wrapper     包装器
     * @param <T>         读取结果泛型
     * @return 读取结果集合
     * @throws Exception 读取异常
     */
    public static <T> List<T> read(InputStream inputStream, ExcelReaderWrapper<T> wrapper) throws Exception {
        CommonExcelReader reader = new CommonExcelReader();
        reader.setWrapper(wrapper);
        return reader.readExcel(inputStream);
    }


    public static void write(OutputStream outputStream, List<Header> headers, List<Object> dataList) throws Exception {
        ExcelWriterConfig config = new ExcelWriterConfig();
        config.setHeaders(headers);
        config.setDatas(dataList);
        write(outputStream, config);
    }

    public static void write(OutputStream outputStream, ExcelWriterConfig config, ExcelWriterConfig... moreSheet) throws Exception {
        CommonExcelWriter writer = new CommonExcelWriter();
        writer.write(outputStream, config, moreSheet);
    }


}
