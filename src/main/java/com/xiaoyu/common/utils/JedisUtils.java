/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.common.utils;


import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.xiaoyu.common.configuration.EncodeFilterConfiguration;

/**
 * Jedis工具
 * 
 * @author xiaoyu 2016年3月19日
 */
public class JedisUtils {

	private static Logger logger = Logger
			.getLogger(EncodeFilterConfiguration.class);

	private static JedisPool pool = SpringBeanUtils.getBean("jedisPool");

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
			jedis = pool.getResource();
			if (jedis.exists(key)) {
				value = jedis.get(key);
			}
		} catch (Exception e) {
			logger.debug("获取key:" + key + "失败", e);
		} finally {
			jedis.close();
		}
		return value;
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
		byte a[] = SerializeUtil.serialize(key);
		try {
			jedis = pool.getResource();
			if (jedis.exists(a)) {
				value = SerializeUtil.unserialize(jedis.get(a));
			}
		} catch (Exception e) {
			logger.debug("获取Object:" + key + "失败", e);
		} finally {
			jedis.close();
		}
		return value;
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
			jedis = pool.getResource();
			result = jedis.set(key, value);
			if (cacheSeconds != 0) {
				jedis.expire(key, cacheSeconds);
			}
		} catch (Exception e) {
			logger.debug("设置key:" + key + "失败", e);
		} finally {
			jedis.close();
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
		byte a[] = SerializeUtil.serialize(key);
		try {
			jedis = pool.getResource();
			result = jedis.set(a, SerializeUtil.serialize(value));
			if (cacheSeconds != 0) {
				jedis.expire(key, cacheSeconds);
			}
		} catch (Exception e) {
			logger.debug("设置对象:" + key + "失败", e);
		} finally {
			jedis.close();
		}
		return result;
	}

}
