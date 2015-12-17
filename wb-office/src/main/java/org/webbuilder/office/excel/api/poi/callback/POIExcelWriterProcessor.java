package org.webbuilder.office.excel.api.poi.callback;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.webbuilder.office.excel.config.ExcelWriterProcessor;

import java.io.OutputStream;

/**
 * Created by 浩 on 2015-12-16 0016.
 */
public class POIExcelWriterProcessor implements ExcelWriterProcessor {

    private OutputStream outputStream;
    private Workbook workbook;
    private Sheet sheet;
    private boolean started;
    private boolean done;
    private int sheetIndex = 0;
    private Row nowRow;
    private Cell nowCell;

    public POIExcelWriterProcessor(OutputStream outputStream, Workbook workbook) {
        this.outputStream = outputStream;
        this.workbook = workbook;
    }

    @Override
    public <S> S start() throws Exception {
        return start("表格" + sheetIndex);
    }

    @Override
    public <S> S start(String sheetName) throws Exception {
        if (started) {
            throw new NullPointerException("processor is stared!");
        }
        sheet = workbook.createSheet(sheetName);
        started = true;
        return (S) sheet;
    }

    @Override
    public <R> R nextRow() {
        int rowNum = nowRow != null ? nowRow.getRowNum() + 1 : 0;
        nowRow = sheet.createRow(rowNum);
        nowCell = null;
        return (R) nowRow;
    }

    @Override
    public <C> C nextCell() {
        int cellNum = nowCell != null ? nowCell.getColumnIndex()+1 : 0;
        nowCell = nowRow.createCell(cellNum);
        return (C) nowCell;
    }

    @Override
    public void done() throws Exception {
        if (done) {
            throw new NullPointerException("processor is done");
        }
        workbook.write(outputStream);
        done = true;
    }

    public int getSheetIndex() {
        return sheetIndex;
    }

    public void setSheetIndex(int sheetIndex) {
        this.sheetIndex = sheetIndex;
    }
}
