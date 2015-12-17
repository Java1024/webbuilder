package org.webbuilder.utils.base;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 浩 on 2015-11-21 0021.
 */
public class ThreadLocalUtil {
    private final static Map<String, ThreadLocal> base = new ConcurrentHashMap<>();


    public static <T> T get(String key) {
        ThreadLocal<T> local = base.get(key);
        if (local != null) {
            return local.get();
        }
        return null;
    }

    public static <T> T get(String key, T defVal) {
        ThreadLocal<T> local = base.get(key);
        if (local != null) {
            return local.get();
        }
        return defVal;
    }

    public static <T> T put(String key, T value) {
        ThreadLocal<T> local = base.get(key);
        if (local == null) {
            local = new ThreadLocal<>();
            base.put(key, local);
        }
        local.set(value);
        return value;
    }


    public static void remove(String key) {
        base.remove(key);
    }

}
