package org.webbuilder.office.excel.config;

/**
 * Created by 浩 on 2015-12-07 0007.
 */
public abstract class AbstractExcelReaderCallBack implements ExcelReaderCallBack {

    protected boolean shutdown;

    @Override
    public boolean isShutdown() {
        return shutdown;
    }

    @Override
    public void shutdown() {
        shutdown = true;
    }
}
