package org.webbuilder.sql;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zhouhao on 16-4-14.
 */
public interface ValueWrapper {
    Object getValue();

    String toString();

    int toInt();

    double toDouble();

    boolean toBoolean();

    int toInt(int defaultValue);

    double toDouble(double defaultValue);

    boolean toBoolean(boolean defaultValue);

    Date toDate();

    Date toDate(String format);

    Map<String, Object> toMap();

    List<Map> toList();

    <T> T toBean(Class<T> type);

    <T> List<T> toBeanList(Class<T> type);

    boolean valueTypeOf(Class<?> type);
}
