package org.webbuilder.office.excel.config;

/**
 * excel写出回掉,api在进行excel写出时,通过调用回掉来进行更多操作
 * Created by 浩 on 2015-12-07 0007.
 */
public interface ExcelWriterCallBack {
    /**
     * 在写出开始前,设置了跳过写出的行时调用。
     * 如果配置中设置了startWith,则在渲染被跳过的单元格时,将调用此回掉来获取自定义的值
     *
     * @param row    当前行
     * @param column 当前列
     * @return 自定义值
     */
    Object startBefore(int row, int column);

    /**
     * 获取一个单元格的自定义样式
     *
     * @param row    行,如果为-1,代表为表头行
     * @param column 列
     * @param header 表头
     * @param value  单元格值
     * @return 自定义样式
     */
    CustomCellStyle getCellStyle(int row, int column, String header, Object value);

    /**
     * 获取自定义列的样式
     *
     * @param column 列号
     * @param header 表头
     * @return 自定义列样式
     */
    CustomColumnStyle getColumnStyle(int column, String header);

    /**
     * 获取自定义行样式
     *
     * @param row    行号，为-1时代表是表头行
     * @param header 表头
     * @return 自定义行样式
     */
    CustomRowStyle getRowStyle(int row, String header);

}
