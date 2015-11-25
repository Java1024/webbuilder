package org.webbuilder.utils.storage.counter;

/**
 * Created by 浩 on 2015-11-25 0025.
 */
public interface Counter {

    String getName();

    long init(String key,long count);

    long next(String key);

    long count(String key);

    long reset(String key);
}
