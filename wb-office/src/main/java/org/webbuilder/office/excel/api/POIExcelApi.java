package org.webbuilder.office.excel.api;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;
import org.webbuilder.office.excel.ExcelApi;
import org.webbuilder.office.excel.config.ExcelReaderCallBack;

import java.io.InputStream;

/**
 * Created by 浩 on 2015-12-07 0007.
 */
public class POIExcelApi implements ExcelApi {
    @Override
    public void read(InputStream inputStream, ExcelReaderCallBack callBack) throws Exception {
        // POIFSFileSystem fs = new POIFSFileSystem(input);
        // 兼容读取 支持2007 +
        Workbook wbs = WorkbookFactory.create(inputStream);
        for (int x = 0; x < wbs.getNumberOfSheets(); x++) {
            Sheet sheet = wbs.getSheetAt(x);
            // 得到总行数
            int rowNum = sheet.getLastRowNum();
            Row row = sheet.getRow(0);
            int colNum = row.getPhysicalNumberOfCells();
            for (int i = 0; i <= rowNum; i++) {
                row = sheet.getRow(i);
                for (int j = 0; j < colNum; j++) {
                    if (callBack.isShutdown()) {
                        return;
                    }
                    ExcelReaderCallBack.CellContent cellContent = new ExcelReaderCallBack.CellContent();
                    cellContent.setFirst(j == 0);
                    cellContent.setLast(j == colNum - 1);
                    cellContent.setSheet(x);
                    cellContent.setRow(i);
                    cellContent.setColumn(j);
                    Object value = row == null ? null : cell2Object(row.getCell(j));
                    cellContent.setValue(value);
                    callBack.onCell(cellContent);
                }
            }
        }
    }


    protected Object cell2Object(Cell cell) {
        if (cell == null)
            return "";
        switch (cell.getCellType()) {
            case HSSFCell.CELL_TYPE_BOOLEAN:
                return cell.getBooleanCellValue();
            case HSSFCell.CELL_TYPE_NUMERIC:
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                }
                return cell.getNumericCellValue();
            case HSSFCell.CELL_TYPE_STRING:
                return cell.getRichStringCellValue().getString();
            default:
                return "";
        }
    }
}
