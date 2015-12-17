package org.webbuilder.office.excel.config;

/**
 * excel写出回掉,api在进行excel写出时,通过调用回掉来进行更多操作
 * Created by 浩 on 2015-12-07 0007.
 */
public interface ExcelWriterCallBack {

    void render(ExcelWriterProcessor processor);

}
