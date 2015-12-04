package org.webbuilder.utils.storage.counter.support.local;

import org.webbuilder.utils.storage.counter.Counter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 本地计数器支持,在对一个key进行计数之前，建议先通过init进行初始化。否则如果并发下可能会导致数据异常
 * Created by 浩 on 2015-11-25 0025.
 */
public class LocalCounter implements Counter {
    //计数器库
    private Map<String, MutableLong> counter = new ConcurrentHashMap<>();
    //计数器名称
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 根据要计数的key 获取单个计数器名称
     *
     * @param key 要计数的key
     * @return 计数器对象
     */
    private MutableLong getMutable(String key) {
        MutableLong mutableLong = counter.get(key);
        if (mutableLong == null) {
            //同步并进行安全初始化，防止首次并发进行计数，导致计数错误
            synchronized (counter) {
                init(key, 0, true);
            }
            return getMutable(key);
        }
        return mutableLong;
    }

    @Override
    public String getName() {
        if (name == null)
            name = "local";
        return name;
    }

    @Override
    public long init(String key, long count) {
        //强制初始化
        return init(key, count, false);
    }

    /**
     * 带有安全模式的初始化，如果为安全模式，计数器已存在，则忽略。否则将替换旧的计数器
     *
     * @param key   要初始化的key
     * @param count 指定默认计数器
     * @param safe  是否为安全模式
     * @return
     */
    public long init(String key, long count, boolean safe) {
        MutableLong mutableLong = counter.get(key);
        //如果为首次初始化或者禁用安全模式则进行初始化
        if (mutableLong == null || !safe) {
            mutableLong = new MutableLong();
            mutableLong.init(count);
            counter.put(key, mutableLong);
        }
        return count;
    }

    @Override
    public long next(String key) {
        return next(key, 1);
    }

    @Override
    public long count(String key) {
        MutableLong mutableLong = getMutable(key);
        synchronized (mutableLong) {
            return mutableLong.count();
        }
    }

    @Override
    public long reset(String key) {
        return getMutable(key).init(0);
    }

    @Override
    public long next(String key, long count) {
        return getMutable(key).next(count);
    }

    /**
     * 可变Long类型，用于对单个key进行计数
     */
    private class MutableLong {
        //当前计数
        private long counter = 0;

        private final ReentrantLock lock = new ReentrantLock();

        /**
         * 初始化
         *
         * @param l 要初始化的值
         * @return 初始化后的值
         */
        private long init(long l) {
            lock.lock();
            try {
                counter = l;
                return counter;
            } finally {
                lock.unlock();
            }
        }

        /**
         * 进行计数
         *
         * @param next 要递增的值
         * @return 计数后的值
         */
        public long next(long next) {
            lock.lock();
            try {
                counter += next;
                return counter;
            } finally {
                lock.unlock();
            }
        }

        /**
         * 获取当前计数
         *
         * @return 当前计数
         */
        public long count() {
            return counter;
        }
    }

}
