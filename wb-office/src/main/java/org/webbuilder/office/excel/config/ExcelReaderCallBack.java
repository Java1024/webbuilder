package org.webbuilder.office.excel.config;

/**
 * excel读取回掉
 * Created by 浩 on 2015-12-07 0007.
 */
public interface ExcelReaderCallBack {

    /**
     * 读取一个单元格时回掉
     *
     * @param content 读取到的单元格内容
     */
    void onCell(CellContent content) throws Exception;

    boolean isShutdown();

    void shutdown();

    /**
     * 单元格内容
     */
    class CellContent {
        /**
         * 当前所在表格
         */
        private int sheet;

        /**
         * 当前所在行
         */
        private int row;

        /**
         * 当前所在列
         */
        private int column;

        /**
         * 当前获取到的值
         */
        private Object value;

        private boolean first;

        private boolean last;

        public int getRow() {
            return row;
        }

        public void setRow(int row) {
            this.row = row;
        }

        public int getColumn() {
            return column;
        }

        public void setColumn(int column) {
            this.column = column;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public int getSheet() {
            return sheet;
        }

        public void setSheet(int sheet) {
            this.sheet = sheet;
        }

        public boolean isFirst() {
            return first;
        }

        public void setFirst(boolean first) {
            this.first = first;
        }

        public boolean isLast() {
            return last;
        }

        public void setLast(boolean last) {
            this.last = last;
        }

        @Override
        public String toString() {
            return "CellContent{" +
                    "sheet=" + sheet +
                    ", row=" + row +
                    ", column=" + column +
                    ", value=" + value +
                    '}';
        }
    }
}
