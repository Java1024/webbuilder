package org.webbuilder.office.excel.support;

import org.webbuilder.office.excel.ExcelApi;
import org.webbuilder.office.excel.ExcelWriter;
import org.webbuilder.office.excel.api.POIExcelApi;
import org.webbuilder.office.excel.config.ExcelWriterConfig;

import java.io.OutputStream;

/**
 * Created by 浩 on 2015-12-07 0007.
 */
public class CommonExcelWriter implements ExcelWriter {
    private ExcelApi api = POIExcelApi.getInstance();

    public ExcelApi getApi() {
        return api;
    }

    public void setApi(ExcelApi api) {
        this.api = api;
    }

    @Override
    public void write(OutputStream outputStream, ExcelWriterConfig config, ExcelWriterConfig... moreSheet) throws Exception {
        getApi().write(outputStream, config, moreSheet);
    }
}
