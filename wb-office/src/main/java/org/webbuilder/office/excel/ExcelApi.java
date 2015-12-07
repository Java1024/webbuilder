package org.webbuilder.office.excel;

import org.webbuilder.office.excel.config.ExcelReaderCallBack;
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

    /**
     * 写出数据到表格，可指定多个sheet进行写出
     *
     * @param outputStream excel输出流
     * @param config       excel写出配置，通过配置可自定义样式等操作
     * @param moreSheet    多个sheet写出
     * @throws Exception 写出异常
     */
    void write(OutputStream outputStream, ExcelWriterConfig config, ExcelWriterConfig... moreSheet) throws Exception;
}
