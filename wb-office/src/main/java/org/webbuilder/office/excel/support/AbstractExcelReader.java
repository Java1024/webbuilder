package org.webbuilder.office.excel.support;

import org.webbuilder.office.excel.ExcelApi;
import org.webbuilder.office.excel.ExcelReader;
import org.webbuilder.office.excel.ExcelReaderWrapper;
import org.webbuilder.office.excel.api.POIExcelApi;
import org.webbuilder.office.excel.config.AbstractExcelReaderCallBack;
import org.webbuilder.office.excel.config.ExcelReaderCallBack;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by 浩 on 2015-12-07 0007.
 */
public abstract class AbstractExcelReader<T> implements ExcelReader<T> {

    protected ExcelApi api = new POIExcelApi();

    @Override
    public List<T> readExcel(InputStream inputStream) throws Exception {
        final List<T> dataList = new ArrayList<>();
        //回掉
        ExcelReaderCallBack callBack = new AbstractExcelReaderCallBack() {
            List<String> header = new LinkedList<>();//表头信息

            //行缓存,一行的数据缓存起来,读完一样进行对象包装后,清空,进行下一行读取
            List<ExcelReaderCallBack.CellContent> temp = new LinkedList<>();
            @Override
            public void onCell(CellContent content) throws Exception {
                //已经被手动终止
                if (getWrapper().isShutdown()) {
                    shutdown();
                    return;
                }
                boolean isHeader = isHeader(content, header);
                if (isHeader) {
                    header.add(String.valueOf(content.getValue()));
                } else {
                    temp.add(content);
                    if (content.isLast()) {
                        dataList.add(wrapperRow(header, temp));
                        temp.clear();
                    }
                }
            }
        };
        api.read(inputStream, callBack);
        return dataList;
    }

    protected T wrapperRow(List<String> headers, List<ExcelReaderCallBack.CellContent> contents) throws Exception {
        T instance = getWrapper().newInstance();
        for (int i = 0, len = contents.size(); i < len; i++) {
            String header = null;
            if (headers.size() >= i) {
                header = headers.get(i);
            }
            getWrapper().wrapper(instance, header, contents.get(i).getValue());
        }
        getWrapper().wrapperDone(instance);
        return instance;
    }

    public abstract ExcelReaderWrapper<T> getWrapper();

    protected boolean isHeader(ExcelReaderCallBack.CellContent content, List<String> header) {
        if (content.getRow() == 0) return true;
        return false;
    }
}
