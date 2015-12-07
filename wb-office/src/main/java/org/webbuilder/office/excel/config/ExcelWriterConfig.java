package org.webbuilder.office.excel.config;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by 浩 on 2015-12-07 0007.
 */
public class ExcelWriterConfig {

    /**
     * 表格名称
     */
    private String sheetName;

    /**
     * 写出的起始位置,
     */
    private int startWith = 0;

    /**
     * 合并相同列
     */
    private List<String> mergeColumns = new LinkedList<>();

    /**
     * 合并指定单元格
     */
    private List<Merge> merges = new LinkedList<>();

    /**
     * 导出的表头信息
     */
    private List<Header> headers = new LinkedList<>();

    /**
     * 导出的数据
     */
    private List<Object> datas = new LinkedList<>();

    /**
     * 写出回掉
     */
    private ExcelWriterCallBack callBack = new CommonExcelWriterCallBack();

    public ExcelWriterConfig addHeader(String header, String field) {
        this.addHeader(new Header(header, field));
        return this;
    }

    public ExcelWriterConfig addHeader(Header header) {
        headers.add(header);
        return this;
    }

    public ExcelWriterConfig addData(Object data) {
        datas.add(data);
        return this;
    }

    public ExcelWriterConfig addAll(List<Object> data) {
        datas.addAll(data);
        return this;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public int getStartWith() {
        return startWith;
    }

    public void setStartWith(int startWith) {
        this.startWith = startWith;
    }


    public ExcelWriterConfig mergeColumn(String... column) {
        mergeColumns.addAll(Arrays.asList(column));
        return this;
    }

    public List<String> getMergeColumns() {
        return mergeColumns;
    }

    public void setMergeColumns(List<String> mergeColumns) {
        this.mergeColumns = mergeColumns;
    }

    public List<Merge> getMerges() {
        return merges;
    }

    public void setMerges(List<Merge> merges) {
        this.merges = merges;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public void setHeaders(List<Header> headers) {
        this.headers = headers;
    }

    public List<Object> getDatas() {
        return datas;
    }

    public void setDatas(List<Object> datas) {
        this.datas = datas;
    }

    public ExcelWriterCallBack getCallBack() {
        return callBack;
    }

    public void setCallBack(ExcelWriterCallBack callBack) {
        this.callBack = callBack;
    }

    public void addMerge(int rowFrom, int colFrom, int rowTo, int colTo) {
        addMerge(new Merge(rowFrom, colFrom, rowTo, colTo));
    }

    public void addMerge(Merge merge) {
        if (!merges.contains(merge)) ;
        merges.add(merge);
    }

    /**
     * 合并信息
     */
    public class Merge {
        private int rowFrom;

        private int colFrom;

        private int rowTo;

        private int colTo;

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Merge) {
                Merge m = (Merge) obj;
                return m.getColFrom() == this.getColFrom() && m.getColTo() == this.getColTo() && m.getRowFrom() == this.getRowFrom() && m.getRowTo() == this.getRowTo();
            }
            return super.equals(obj);
        }

        public Merge(int rowFrom, int colFrom, int rowTo, int colTo) {
            this.rowFrom = rowFrom;
            this.colFrom = colFrom;
            this.rowTo = rowTo;
            this.colTo = colTo;
        }

        public int getRowFrom() {
            return rowFrom;
        }

        public void setRowFrom(int rowFrom) {
            this.rowFrom = rowFrom;
        }

        public int getColFrom() {
            return colFrom;
        }

        public void setColFrom(int colFrom) {
            this.colFrom = colFrom;
        }

        public int getRowTo() {
            return rowTo;
        }

        public void setRowTo(int rowTo) {
            this.rowTo = rowTo;
        }

        public int getColTo() {
            return colTo;
        }

        public void setColTo(int colTo) {
            this.colTo = colTo;
        }

        @Override
        public String toString() {
            return "Merge{" +
                    "rowFrom=" + rowFrom +
                    ", colFrom=" + colFrom +
                    ", rowTo=" + rowTo +
                    ", colTo=" + colTo +
                    '}';
        }
    }
}
