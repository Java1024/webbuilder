package org.webbuilder.office.excel.config;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import java.util.Date;

/**
 * Created by 浩 on 2015-12-07 0007.
 */
public class CommonExcelWriterCallBack implements ExcelWriterCallBack {

    private CustomCellStyle cellStyle = new CustomCellStyle();


    public CommonExcelWriterCallBack() {
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        CustomCellStyle.Border border = new CustomCellStyle.Border((short) 1, HSSFColor.BLACK.index);
        cellStyle.setBorderLeft(border);
        cellStyle.setBorderRight(border);
        cellStyle.setBorderBottom(border);
        cellStyle.setBorderTop(border);
    }

    @Override
    public Object startBefore(int row, int column) {
        return "";
    }

    @Override
    public CustomCellStyle getCellStyle(int row, int column, String header, Object value) {
        if (value == null) {
            cellStyle.setDataType("string");
        } else {
            if (value instanceof Integer) {
                cellStyle.setDataType("int");
            } else if (value instanceof Number) {
                cellStyle.setDataType("double");
            } else if (value instanceof Date) {
                cellStyle.setDataType("date");
                cellStyle.setFormat("yyyy-MM-dd");
            } else {
                cellStyle.setDataType("string");
            }
        }
        cellStyle.setValue(value);
        return cellStyle;
    }

    @Override
    public CustomColumnStyle getColumnStyle(int column, String header) {
        return null;
    }

    @Override
    public CustomRowStyle getRowStyle(int row, String header) {
        return null;
    }
}
