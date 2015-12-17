package org.webbuilder.office.excel;

import org.webbuilder.office.excel.config.ExcelReaderCallBack;
import org.webbuilder.office.excel.config.ExcelWriterCallBack;
import org.webbuilder.office.excel.config.ExcelWriterConfig;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * excel操作的API接口
 * Created by 浩 on 2015-12-07 0007.
 */
public interface ExcelApi {

    /**
     * 基于回掉的excel读取
     *
     * @param inputStream excel文件输入流
     * @param callBack    excel读取回掉接口
     * @throws Exception 读取异常
     */
    void read(InputStream inputStream, ExcelReaderCallBack callBack) throws Exception;

    void write(OutputStream outputStream, ExcelWriterCallBack callBack, ExcelWriterCallBack... moreSheet) throws Exception;
}
