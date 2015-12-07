package org.webbuilder.office.excel;

import java.io.InputStream;
import java.util.List;

/**
 * Created by 浩 on 2015-12-07 0007.
 */
public interface ExcelReader<T> {
    List<T> readExcel(InputStream inputStream) throws Exception;
}
