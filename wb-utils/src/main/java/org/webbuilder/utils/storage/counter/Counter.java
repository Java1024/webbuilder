package org.webbuilder.utils.storage.counter;

/**
 * 计数器接口，用于指定一个key进行计数
 * Created by 浩 on 2015-11-25 0025.
 */
public interface Counter {

    /**
     * 获取计数器名称
     *
     * @return 计数器名称
     */
    String getName();

    /**
     * 初始化计数器并指定初始计数值
     *
     * @param key   要初始化的key
     * @param count 初始计数值
     * @return 当前计数值
     */
    long init(String key, long count);

    /**
     * 对指定的key进行计数，默认+1
     *
     * @param key 指定的key
     * @return 当前计数
     */
    long next(String key);

    /**
     * 对指定的key进行指定计数
     *
     * @param key   指定的key
     * @param count 指定计数
     * @return 当前计数
     */
    long next(String key, long count);

    /**
     * 指定key获取当前计数
     *
     * @param key 指定key
     * @return 当前计数
     */
    long count(String key);

    /**
     * 对指定key的计数器重置，相当于init(key,0)
     *
     * @param key 指定key
     * @return 重置后的计数
     */
    long reset(String key);
}
