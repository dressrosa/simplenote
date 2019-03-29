/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.simplenote.common.utils;

import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Jedis工具
 * 
 * @author xiaoyu 2016年3月19日
 */
@Component
public class JedisUtils {

    protected static Logger logger = LoggerFactory.getLogger(JedisUtils.class);

    private static JedisPool pool;

    @Autowired
    @Qualifier("jedisPool")
    public void setPool(JedisPool jedisPool) {
        JedisUtils.pool = jedisPool;
    }

    public static Jedis getRedis() {
        return JedisUtils.pool.getResource();
    }

    public static boolean exists(String key) {
        Jedis jedis = null;
        try {
            jedis = JedisUtils.pool.getResource();
            return jedis.exists(key);
        } catch (final Exception e) {
            logger.debug("获取key:" + key + "失败");
            return false;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 获取key
     * 
     * @author xiaoyu
     * @param key
     * @return
     * @time 2016年3月19日下午8:34:34
     */
    public static String get(String key) {
        String value = null;
        Jedis jedis = null;
        try {
            jedis = JedisUtils.pool.getResource();
            value = jedis.get(key);
        } catch (final Exception e) {
            JedisUtils.logger.debug("获取key:" + key + "失败");
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return value;
    }

    public static long ttl(String key) {
        Jedis jedis = null;
        try {
            jedis = JedisUtils.pool.getResource();
            return jedis.ttl(key);
        } catch (final Exception e) {
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return -1;
    }

    public static long del(String key) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = JedisUtils.pool.getResource();
            if (jedis.exists(key)) {
                result = jedis.del(key);
            }
        } catch (final Exception e) {
            JedisUtils.logger.debug("del:" + key + "失败");
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return result;
    }

    /**
     * 获取对象
     * 
     * @author xiaoyu
     * @param key
     * @return
     * @time 2016年3月19日下午8:37:58
     */
    public static Object getObject(String key) {
        Object value = null;
        Jedis jedis = null;
        try {
            jedis = JedisUtils.pool.getResource();
            if (jedis.exists(key)) {
                value = JSON.parseObject(jedis.get(key));
            }
        } catch (final Exception e) {
            JedisUtils.logger.debug("获取Object:" + key + "失败");
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return value;
    }

    public static long expire(String key, int cacheSeconds) {
        Jedis jedis = null;
        try {
            jedis = JedisUtils.pool.getResource();
            if (cacheSeconds > 0) {
                return jedis.expire(key, cacheSeconds);
            }
        } catch (final Exception e) {
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    /**
     * 设置key
     * 
     * @author xiaoyu
     * @param key
     * @param value
     * @param cacheSeconds
     * @return
     * @time 2016年3月19日下午8:47:18
     */
    public static String set(String key, String value, int cacheSeconds) {
        String result = null;
        Jedis jedis = null;
        try {
            jedis = JedisUtils.pool.getResource();
            result = jedis.set(key, value);
            if (cacheSeconds > 0) {
                jedis.expire(key, cacheSeconds);
            }
        } catch (final Exception e) {
            e.printStackTrace();
            JedisUtils.logger.debug("设置key:" + key + "失败");
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return result;
    }

    public static boolean setnx(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = JedisUtils.pool.getResource();
            return jedis.setnx(key, value) == 1 ? true : false;
        } catch (final Exception e) {
            e.printStackTrace();
            logger.debug("设置key:" + key + "失败");
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }

    public static String getSet(String key, String value) {
        String result = null;
        Jedis jedis = null;
        try {
            jedis = JedisUtils.pool.getResource();
            result = jedis.getSet(key, value);
        } catch (final Exception e) {
            e.printStackTrace();
            logger.debug("设置key:" + key + "失败");
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return result;
    }

    public static long hincrby(String key, String field, long value) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = JedisUtils.pool.getResource();
            result = jedis.hincrBy(key, field, value);
        } catch (final Exception e) {
            JedisUtils.logger.debug("hincrby:" + key + "失败");
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return result;
    }

    public static String hget(String key, String field) {
        String result = null;
        Jedis jedis = null;
        try {
            jedis = JedisUtils.pool.getResource();
            result = jedis.hget(key, field);
        } catch (final Exception e) {
            JedisUtils.logger.debug("hget:" + key + "失败");
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return result;
    }

    public static long hset(String key, String field, String value, int seconds) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = JedisUtils.pool.getResource();
            result = jedis.hset(key, field, value);
            jedis.expire(key, seconds);
        } catch (final Exception e) {
            JedisUtils.logger.debug("hget:" + key + "失败");
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return result;
    }

    /**
     * 设置对象
     * 
     * @author xiaoyu
     * @param key
     * @param value
     * @param cacheSeconds
     * @return
     * @time 2016年3月19日下午8:47:53
     */
    public static String setObject(String key, Object value, int cacheSeconds) {
        String result = null;
        Jedis jedis = null;
        try {
            jedis = JedisUtils.pool.getResource();
            result = jedis.set(key, JSON.toJSONString(value));
            if (cacheSeconds > 0) {
                jedis.expire(key, cacheSeconds);
            }
        } catch (final Exception e) {
            JedisUtils.logger.debug("设置对象:" + key + "失败");
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return result;
    }

    /**
     * set集合
     * 
     * @param key
     * @param value
     * @return
     */
    public static long sadd(String key, String value) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = JedisUtils.pool.getResource();
            result = jedis.sadd(key, value);
        } catch (final Exception e) {
            JedisUtils.logger.debug("设置set集合:" + key + "失败");
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return result;
    }

    /**
     * 删除set集合中元素
     * 
     * @param key
     * @param value
     * @return
     */
    public static long sremove(String key, String value) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = JedisUtils.pool.getResource();
            result = jedis.srem(key, value);
        } catch (final Exception e) {
            JedisUtils.logger.debug("删除元素set集合:" + key + "失败");
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return result;
    }

    /**
     * 获取set集合所有元素
     * 
     * @param key
     * @return
     */
    public static Set<String> smembers(String key) {
        Set<String> result = null;
        Jedis jedis = null;
        try {
            jedis = JedisUtils.pool.getResource();
            result = jedis.smembers(key);
        } catch (final Exception e) {
            JedisUtils.logger.debug("获取set集合:" + key + "失败");
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return result;
    }

    /**
     * 获取set集合元素数量
     * 
     * @param key
     * @return
     */
    public static long scard(String key) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = JedisUtils.pool.getResource();
            result = jedis.scard(key);
        } catch (final Exception e) {
            JedisUtils.logger.debug("获取set集合数目:" + key + "失败");
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return result;
    }
}
