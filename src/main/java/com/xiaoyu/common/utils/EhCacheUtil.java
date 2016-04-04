/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.common.utils;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * ecache工具类
 *  
 * @author xiaoyu 2016年3月18日
 */
@EnableAutoConfiguration
public class EhCacheUtil {

	private static CacheManager cacheManager = SpringBeanUtils.getBean("ecacheManager");

	/**
	 * 获取缓存内的值
	 * 
	 * @author xiaoyu
	 * @param cacheName
	 * @param key
	 * @return
	 * @time 2016年3月18日下午4:37:10
	 */
	public static Object get(String cacheName, String key) {
		Cache c = getEhCache(cacheName);
		Element e = c.get(key);
		if (e != null) {
			return e.getObjectValue();
		}
		return null;
	}

	/**
	 * 存入缓存
	 * 
	 * @author xiaoyu
	 * @param cacheName
	 * @param key
	 * @param value
	 * @time 2016年3月18日下午4:39:48
	 */
	public static void put(String cacheName, String key, Object value) {
		Cache c = getEhCache(cacheName);
		Element e = new Element(key, value);
		c.put(e);
	}
	
	/**是否存在
	 *@author xiaoyu
	 *@param cacheName
	 *@return
	 *@time 2016年3月21日下午4:09:52
	 */
	public static boolean IsExist(String cacheName) {
		return cacheManager.cacheExists(cacheName);
		
	}
	/**
	 * 删除缓存
	 * 
	 * @author xiaoyu
	 * @param cacheName
	 * @param key
	 * @time 2016年3月18日下午4:39:55
	 */
	public static void remove(String cacheName, String key) {
		Cache c = getEhCache(cacheName);
		c.remove(key);
	}

	/**
	 * 获取缓存
	 * 
	 * @author xiaoyu
	 * @param cacheName
	 * @return
	 * @time 2016年3月18日下午4:30:21
	 */
	private static Cache getEhCache(String cacheName) {
		Cache cache = null;
		if (cacheManager.cacheExists(cacheName)) {
			cache = cacheManager.getCache(cacheName);
		} else {
			cacheManager.addCache(cacheName);
			cache = cacheManager.getCache(cacheName);
		}
		return cache;
	}

	public static CacheManager getCacheManager() {
		return cacheManager;
	}
}
