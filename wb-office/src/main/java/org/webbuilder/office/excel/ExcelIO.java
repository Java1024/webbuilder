package org.webbuilder.office.excel;

import org.webbuilder.office.excel.support.CommonExcelReader;
import org.webbuilder.office.excel.wrapper.BeanWrapper;
import org.webbuilder.office.excel.wrapper.HashMapWrapper;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by 浩 on 2015-12-07 0007.
 */
public class ExcelIO {

    public static List<Map<String, Object>> read2Map(InputStream inputStream) throws Exception {
        return read(inputStream, new HashMapWrapper());
    }

    public static <T> List<T> read2Bean(InputStream inputStream, Map<String, String> headerMapper) throws Exception {
        BeanWrapper beanWrapper = new BeanWrapper<T>();
        beanWrapper.setHeaderNameMapper(headerMapper);
        return read(inputStream, beanWrapper);
    }

    public static <T> List<T> read2Bean(InputStream inputStream, Map<String, String> headerMapper, Class<T> tClass) throws Exception {
        BeanWrapper wrapper = new BeanWrapper<T>();
        wrapper.setType(tClass);
        wrapper.setHeaderNameMapper(headerMapper);
        return read(inputStream, wrapper);
    }

    public static <T> List<T> read(InputStream inputStream, ExcelReaderWrapper<T> wrapper) throws Exception {
        CommonExcelReader reader = new CommonExcelReader();
        reader.setWrapper(wrapper);
        return reader.readExcel(inputStream);
    }
}
