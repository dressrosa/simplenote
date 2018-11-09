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

    private static final int Expire_Seconds = 3600;

    /**
     * 加锁数量
     */
    private static final ThreadLocal<Integer> accquires = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return 0;
        }
    };

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

    /**
     * 传入函数,加锁执行,这里runnable只是一个函数接口,
     * 和线程无关
     * 
     * @param r
     */
    public void lock(Runnable r) {
        if (tryLock()) {
            try {
                r.run();
            } finally {
                tryRelease();
            }
        }
    }

    private final boolean tryLock() {
        int n = accquires.get();
        if (n > 1) {
            accquires.set(n + 1);
            return true;
        }
        return this.doLock();
    }

    private final boolean doLock() {
        int n = accquires.get();
        Jedis jedis = JedisUtils.getRedis();
        try {
            lockValue = System.currentTimeMillis() + Expire_Seconds * 1000;
            // 竞争上锁成功
            if (jedis.setnx(key, lockValue + "") == 1) {
                // 这里可能失败
                jedis.expire(key, Expire_Seconds);
                accquires.set(n + 1);
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
                        accquires.set(n + 1);
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

    private final void tryRelease() {
        int n = accquires.get();
        if (n > 1) {
            accquires.set(n - 1);
            return;
        }
        this.doRelease();
    }

    private final void doRelease() {
        // 这里应该重新获取jedis,因为业务代码可能执行了好长时间
        Jedis jedis = JedisUtils.getRedis();
        try {
            String now = jedis.get(key);
            if (now != null && now.equals(lockValue + "")) {
                jedis.del(key);
            }
        } finally {
            // 走到这里,说明其他重入锁已经释放,开始第一层加锁的释放.
            accquires.set(0);
            if (jedis != null) {
                jedis.close();
            }
        }
    }

}
