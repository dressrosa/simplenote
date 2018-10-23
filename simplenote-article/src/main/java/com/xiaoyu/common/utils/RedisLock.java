/**
 * 
 */
package com.xiaoyu.common.utils;

import java.util.function.Supplier;

import redis.clients.jedis.Jedis;

/**
 * @author hongyu
 * @date 2018-10
 * @description 1.可以用于单纯防重操作; 2.可用于分布式锁.
 * 
 *              <pre>
 *              RedisLock lock = RedisLock.getRedisLock("hello");
 *              Integer a = lock.lock(() -> {
 *                  return 1;
 *              });
 *              </pre>
 */
public class RedisLock {

    private static final int Expire_Seconds = 60;

    private RedisLock(String key) {
        this.key = key;
    }

    private String key;
    private long lockValue;

    public static RedisLock getRedisLock(String key) {
        if (key == null || "".equals(key)) {
            return null;
        }
        return new RedisLock(key);
    }

    /**
     * 传入函数,返回执行结果,如果获取锁失败,返回null
     * 
     * @param s
     * @return
     */
    public <T> T lock(Supplier<T> s) {
        if (tryLock()) {
            try {
                return s.get();
            } finally {
                tryRelease();
            }
        }
        return null;
    }

    private boolean tryLock() {
        Jedis jedis = JedisUtils.getRedis();
        try {
            lockValue = System.currentTimeMillis() + Expire_Seconds * 1000;
            // 竞争上锁成功
            if (jedis.setnx(key, lockValue + "") == 1) {
                // 这里可能失败
                jedis.expire(key, Expire_Seconds);
                return true;
            }
            // key已经存在
            else {
                String current = jedis.get(key);
                // 上次没有成功设置expire,现在其实已经过期了
                if (Long.valueOf(current).longValue() < System.currentTimeMillis()) {
                    lockValue = System.currentTimeMillis() + Expire_Seconds * 1000;
                    // 都来竞争上锁
                    String old = jedis.getSet(key, lockValue + "");
                    // 竞争上锁成功
                    if (String.valueOf(current).equals(old)) {
                        jedis.expire(key, Expire_Seconds);
                        return true;
                    } else {
                        return false;
                    }
                }

            }
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }

    private void tryRelease() {
        // 这里应该重新获取jedis,因为业务代码可能执行了好长时间
        Jedis jedis = JedisUtils.getRedis();
        try {
            String now = jedis.get(key);
            if (now != null && now.equals(lockValue + "")) {
                jedis.del(key);
            }
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

}
