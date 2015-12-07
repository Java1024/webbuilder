package org.webbuilder.office.excel.support;

import org.webbuilder.office.excel.ExcelReaderWrapper;

/**
 * Created by 浩 on 2015-12-07 0007.
 */
public class CommonExcelReader<T> extends AbstractExcelReader<T> {

    protected ExcelReaderWrapper<T> wrapper = null;

    @Override
    public ExcelReaderWrapper<T> getWrapper() {
        return wrapper;
    }

    public void setWrapper(ExcelReaderWrapper<T> wrapper) {
        this.wrapper = wrapper;
    }
}
