package org.webbuilder.utils.storage.counter.support.local;

import org.webbuilder.utils.storage.counter.Counter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 浩 on 2015-11-25 0025.
 */
public class LocalCounter implements Counter {
    private Map<String, MutableLong> counter = new ConcurrentHashMap<>();
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    private MutableLong getMutable(String key) {
        MutableLong mutableLong = counter.get(key);
        if (mutableLong == null) {
            mutableLong = new MutableLong();
            counter.put(key, mutableLong);
            return mutableLong;
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
        return getMutable(key).init(count);
    }

    @Override
    public long next(String key) {
        return getMutable(key).next();
    }

    @Override
    public long count(String key) {
        return getMutable(key).count();
    }

    @Override
    public long reset(String key) {
        return getMutable(key).init(0);
    }

    private class MutableLong {
        private long counter = 0;

        private synchronized long init(long l) {
            counter = l;
            return counter;
        }

        public synchronized long next() {
            counter++;
            return counter;
        }

        public long count() {
            return counter;
        }
    }

}
