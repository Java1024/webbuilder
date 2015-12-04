package org.webbuilder.utils.storage.counter.support.redis;

import org.webbuilder.utils.storage.counter.Counter;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * 基于redis hincr的计数器
 * Created by 浩 on 2015-11-25 0025.
 */
public class RedisCounter implements Counter {
    /**
     * redis连接池
     */
    protected ShardedJedisPool pool = null;

    public ShardedJedisPool getPool() {
        return pool;
    }

    public void setPool(ShardedJedisPool pool) {
        this.pool = pool;
    }

    public ShardedJedis getResource() {
        return pool.getResource();
    }

    //计数器名称
    private String name;

    //计数器存在于redis中key的名称
    private String hkey;

    public void setName(String name) {
        this.hkey = "counter.".concat(name);
        this.name = name;
    }

    @Override
    public String getName() {
        if (name == null)
            setName("redis");
        return name;
    }


    @Override
    public long init(String key, long count) {
        try (ShardedJedis jedis = getResource()) {
            return jedis.hincrBy(hkey, key, count);
        }
    }

    @Override
    public long next(String key) {
        return next(key, 1);
    }

    @Override
    public long next(String key, long count) {
        try (ShardedJedis jedis = getResource()) {
            return jedis.hincrBy(hkey, key, count);
        }
    }

    @Override
    public long count(String key) {
        try (ShardedJedis jedis = getResource()) {
            return jedis.hincrBy(hkey, key, 0);
        }
    }

    @Override
    public long reset(String key) {
        try (ShardedJedis jedis = getResource()) {
            return jedis.hdel(hkey, key);
        }
    }
}
