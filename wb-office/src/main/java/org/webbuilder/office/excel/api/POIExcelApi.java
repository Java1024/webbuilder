package org.webbuilder.office.excel.api;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webbuilder.office.excel.ExcelApi;
import org.webbuilder.office.excel.config.*;
import org.webbuilder.office.excel.config.Header;
import org.webbuilder.utils.base.ClassUtil;
import org.webbuilder.utils.base.StringUtil;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * POI的excel读取实现
 * Created by 浩 on 2015-12-07 0007.
 */
public class POIExcelApi implements ExcelApi {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final POIExcelApi instance = new POIExcelApi();

    private POIExcelApi() {
    }

    public static POIExcelApi getInstance() {
        return instance;
    }

    @Override
    public void read(InputStream inputStream, ExcelReaderCallBack callBack) throws Exception {
        // POIFSFileSystem fs = new POIFSFileSystem(input);
        // 兼容读取 支持2007 +
        Workbook wbs = WorkbookFactory.create(inputStream);
        //获取sheets
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
                    //创建单元格数据
                    ExcelReaderCallBack.CellContent cellContent = new ExcelReaderCallBack.CellContent();
                    cellContent.setFirst(j == 0);
                    cellContent.setLast(j == colNum - 1);
                    cellContent.setSheet(x);
                    cellContent.setRow(i);
                    cellContent.setColumn(j);
                    Object value = row == null ? null : cell2Object(row.getCell(j));
                    cellContent.setValue(value);
                    //调用回掉
                    callBack.onCell(cellContent);
                }
            }
        }
    }


    /**
     * 将单元格数据转为java对象
     *
     * @param cell 单元格数据
     * @return 对应的java对象
     */
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


    @Override
    public void write(OutputStream outputStream, ExcelWriterConfig config, ExcelWriterConfig... moreSheet) throws Exception {
        //支持2007写出
        Workbook workbook = new XSSFWorkbook();
        //合并所有需要写出的sheet
        List<ExcelWriterConfig> allSheet = new ArrayList<>(Arrays.asList(moreSheet));
        allSheet.add(0, config);
        int index = 0;
        try {
            for (ExcelWriterConfig writerConfig : allSheet) {
                String name = writerConfig.getSheetName();
                if (name == null) {
                    name = "sheet" + (index++);
                }
                if (logger.isInfoEnabled())
                    logger.info("start build sheet:{}", name);
                //构建sheet
                buildSheet(workbook, workbook.createSheet(name), writerConfig);
                if (logger.isInfoEnabled())
                    logger.info("build sheet:{} success", name);
            }
            //写出
            workbook.write(outputStream);
        } finally {
            //移除单元格样式缓存
            cellStyleCache.remove(workbook);
        }

    }

    /**
     * 构建数据到sheet
     *
     * @param workbook 工作簿
     * @param sheet    表格
     * @param config   配置
     */
    protected void buildSheet(Workbook workbook, Sheet sheet, ExcelWriterConfig config) {
        //回掉接口
        ExcelWriterCallBack callBack = config.getCallBack();
        //表头信息
        List<org.webbuilder.office.excel.config.Header> headers = config.getHeaders();
        //数据
        List<Object> datas = config.getDatas();
        //表头
        Row headerRow = sheet.createRow(config.getStartWith());
        int columnStart = 0, columnEnd = headers.size();
        if (callBack != null) {
            //通过回掉或用表头的样式,row=-1则认为是表头
            CustomRowStyle rowStyle = callBack.getRowStyle(-1, "header");
            if (rowStyle != null) {
                headerRow.setHeightInPoints((float) rowStyle.getHeight());
            }
        }
        if (logger.isInfoEnabled())
            logger.info("header size:{}", headers.size());
        //一列一列的写出
        for (int c = columnStart; c < columnEnd; c++) {
            int rowStart = 0, rowEnd = datas.size() + config.getStartWith();
            Header header = headers.get(c);
            //初始化表头单元格数据
            Cell h_cell = headerRow.createCell(c);
            h_cell.setCellValue(header.getTitle());
            initCell(workbook, -1, c, h_cell, header.getTitle(), header.getTitle(), config);
            if (callBack != null) {
                //通过回掉获取列的样式
                CustomColumnStyle columnStyle = callBack.getColumnStyle(c, header.getTitle());
                if (columnStyle != null) {
                    sheet.setColumnWidth(c, columnStyle.getWidth());
                }
            }
            if (logger.isInfoEnabled())
                logger.info("init column: {}->{}", header.getField(), header.getTitle());
            //-------------------开始遍历数据-------------
            for (int r = rowStart; r < rowEnd; r++) {
                Object value = null;
                Row row;
                //自定了跳过开始行
                if (r < config.getStartWith()) {
                    //通过回掉获取跳过行的自定义单元格数据
                    if (callBack != null) {
                        value = callBack.startBefore(c, r);
                    }
                    //创建行对象
                    row = sheet.getRow(r) == null ? sheet.createRow(r) : sheet.getRow(r);
                } else {
                    Object data = datas.get(r - config.getStartWith());
                    try {
                        //反射获取数据
                        value = ClassUtil.getValueByAttribute(header.getField(), data);
                    } catch (Exception e) {
                    }
                    if (value == null) value = "";
                    //创建行对象
                    row = sheet.getRow(r + 1) == null ? sheet.createRow(r + 1) : sheet.getRow(r + 1);
                }
                Cell cell = row.createCell(c);
                if (r == rowStart) {
                    //通过回掉获取每一行的样式
                    if (callBack != null) {
                        CustomRowStyle rowStyle = callBack.getRowStyle(r, header.getTitle());
                        if (rowStyle != null) {
                            row.setHeightInPoints((float) rowStyle.getHeight());
                        }
                    }
                }
                //初始化单元格数据
                initCell(workbook, r, c, cell, header.getTitle(), value, config);
            }
        }
        if (logger.isInfoEnabled())
            logger.info("prepare merge column");
        //解析表头与列号
        List<String> list = config.getMergeColumns();
        Map<String, Integer> cols = new LinkedHashMap<>();
        for (Header header : headers) {
            if (list.contains(header.getField())) {
                cols.put(header.getField(), headers.indexOf(header));
            }
        }
        //编译合并单元格
        prepareMerges(datas, cols, config);
        // 合并单元格
        if (logger.isInfoEnabled())
            logger.info("start merge column");
        List<ExcelWriterConfig.Merge> merges = config.getMerges();
        for (ExcelWriterConfig.Merge merge : merges) {
            try {
                sheet.addMergedRegion(new CellRangeAddress(merge.getRowFrom() + config.getStartWith(), merge.getColTo() + config.getStartWith(), merge.getColFrom(), merge.getRowTo()));
            } catch (Exception e) {
                logger.error("merge column ({}) error", merge, e);
            }
        }
    }



    /**
     * 初始化单元格
     *
     * @param workbook 工作簿
     * @param r        行号
     * @param c        列号
     * @param cell     单元格对象
     * @param header   表头
     * @param value    单元格数据值
     * @param config   写出配置
     */
    protected void initCell(Workbook workbook, int r, int c, Cell cell, String header, Object value, ExcelWriterConfig config) {
        ExcelWriterCallBack callBack = config.getCallBack();
        CustomCellStyle style;
        //如果通过回掉未获取到自定义样式,则使用默认的样式进行处理
        if (callBack == null || (style = callBack.getCellStyle(r, c, header, value)) == null) {
            initCell(workbook, cell, value);
            return;
        }
        CellStyle cellStyle = getStyle(workbook, style);
        cell.setCellStyle(cellStyle);
        //根据指定的数据类型,转为excel中的值
        switch (style.getDataType()) {
            case "date":
                cell.setCellValue((Date) style.getValue());
                break;
            case "int":
                cell.setCellValue(StringUtil.toInt(style.getValue()));
                break;
            case "double":
                cell.setCellValue(StringUtil.toDouble(style.getValue()));
                break;
            default:
                cell.setCellValue(String.valueOf(style.getValue()));
                break;
        }
    }

    protected void initCell(Workbook workbook, Cell cell, Object value) {
        cell.setCellValue(String.valueOf(value));
    }

    /**
     * 根据自定义样式获取excel单元格样式实例,如果已初始化过则获取缓存中的样式
     *
     * @param workbook        工作簿
     * @param customCellStyle 自定义样式
     * @return 单元格样式实例
     */
    private CellStyle getStyle(Workbook workbook, CustomCellStyle customCellStyle) {
        //尝试获取缓存
        CellStyle style = getStyleFromCache(workbook, customCellStyle.getCacheKey());

        if (style == null) {
            //为获取到缓存则初始化
            style = workbook.createCellStyle();
            //字体
            if (customCellStyle.getFontName() != null || customCellStyle.getFontColor() != 0) {
                Font font = workbook.createFont();
                if (customCellStyle.getFontName() != null) {
                    font.setFontName(customCellStyle.getFontName());
                }
                if (customCellStyle.getFontColor() != 0) {
                    font.setColor(customCellStyle.getFontColor());
                }
                style.setFont(font);
            }
            //表格
            if (customCellStyle.getBorderTop() != null) {
                style.setBorderTop(customCellStyle.getBorderTop().getSize());
                style.setTopBorderColor(customCellStyle.getBorderTop().getColor());
            }
            if (customCellStyle.getBorderBottom() != null) {
                style.setBorderBottom(customCellStyle.getBorderBottom().getSize());
                style.setBottomBorderColor(customCellStyle.getBorderBottom().getColor());
            }
            if (customCellStyle.getBorderLeft() != null) {
                style.setBorderLeft(customCellStyle.getBorderTop().getSize());
                style.setLeftBorderColor(customCellStyle.getBorderTop().getColor());
            }
            if (customCellStyle.getBorderRight() != null) {
                style.setBorderRight(customCellStyle.getBorderTop().getSize());
                style.setRightBorderColor(customCellStyle.getBorderTop().getColor());
            }
            //数据格式
            if (customCellStyle.getFormat() != null) {
                DataFormat dataFormat = workbook.createDataFormat();
                style.setDataFormat(dataFormat.getFormat(customCellStyle.getFormat()));
            }
            // 水平
            style.setAlignment(customCellStyle.getAlignment());
            // 垂直
            style.setVerticalAlignment(customCellStyle.getVerticalAlignment());
            //放入缓存
            putStyleFromCache(workbook, customCellStyle.getCacheKey(), style);
        }
        return style;
    }

    /**
     * 单元格样式缓存
     */
    private Map<Workbook, Map<String, CellStyle>> cellStyleCache = new ConcurrentHashMap<>();

    /**
     * 设置一个工作簿的的单元格样式到缓存中
     *
     * @param workbook  工作簿
     * @param key       缓存key
     * @param cellStyle 单元格样式
     */
    private void putStyleFromCache(Workbook workbook, String key, CellStyle cellStyle) {
        Map<String, CellStyle> cellStyleMap = cellStyleCache.get(workbook);
        if (cellStyleMap == null) {
            cellStyleMap = new HashMap<>();
            cellStyleCache.put(workbook, cellStyleMap);
        }
        cellStyleMap.put(key, cellStyle);
    }

    /**
     * 从缓存中获取指定工作簿的指定样式
     *
     * @param workbook 工作簿
     * @param key      样式key
     * @return 单元格样式，如果没有则返回null
     */
    private CellStyle getStyleFromCache(Workbook workbook, String key) {
        Map<String, CellStyle> cellStyleMap = cellStyleCache.get(workbook);
        if (cellStyleMap != null) {
            return cellStyleMap.get(key);
        }
        return null;
    }


    /**
     * 编译需要合并的列
     *
     * @param dataList 数据集合
     * @param cols     需要合并的列<列名, 列索引>
     * @param config   配置对象
     * @throws Exception
     */
    protected void prepareMerges(List<Object> dataList, Map<String, Integer> cols, ExcelWriterConfig config) {
        // 列所在索引//列计数器////上一次合并的列位置
        int index, countNumber, lastMergeNumber;
        //已合并列的缓存
        List<String> temp = new ArrayList<>();
        // 遍历要合并的列名
        for (String header : cols.keySet()) {
            index = cols.get(header);// 列所在索引
            countNumber = lastMergeNumber = 0;
            Object lastData = null;// 上一行数据
            // 遍历列
            int dataIndex = 0;
            for (Object data : dataList) {
                dataIndex++;
                Object val = null;
                try {
                    val = ClassUtil.getValueByAttribute(header, data);// data.get(header);
                } catch (Exception e) {
                }
                if (val == null)
                    val = "";
                //如果上一列的本行未进行合并,那么这一列也不进行合并
                if (index != 0 && !temp.contains(StringUtil.concat("c_", index - 1, "_d", dataIndex))) {
                    lastData = "__$$";
                }
                // 如果当前行和上一行相同 ，合并列数+1
                if ((val.equals(lastData) || lastData == null)) {
                    countNumber++;
                    temp.add(StringUtil.concat("c_", index, "_d", dataIndex));
                } else {
                    // 与上一行不一致，代表本次合并结束
                    config.addMerge(lastMergeNumber + 1, index, index, countNumber);
                    lastMergeNumber = countNumber;// 记录当前合并位置
                    countNumber++;// 总数加1

                }
                // 列末尾需要合并
                if (dataList.indexOf(data) == dataList.size() - 1) {
                    config.addMerge(lastMergeNumber + 1, index, index, dataList.size());
                    temp.add(StringUtil.concat("c_", index, "_d", dataIndex));
                }
                // 上一行数据
                lastData = val;
            }
        }
    }


}
