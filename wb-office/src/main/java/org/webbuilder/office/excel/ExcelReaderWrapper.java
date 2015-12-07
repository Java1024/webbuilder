package org.webbuilder.office.excel;

/**
 * Created by 浩 on 2015-12-07 0007.
 */
public interface ExcelReaderWrapper<T> {

    boolean isShutdown();

    void shutdown();

    T newInstance() throws Exception;

    void wrapper(T instance, String header, Object value);

    void wrapperDone(T instance);

}
