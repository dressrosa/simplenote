/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.common.configuration;

import javax.annotation.Resource;

import net.sf.ehcache.CacheManager;

import org.apache.log4j.Logger;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

/**
 * 设置ehcache为默认cachemanager
 * 
 * @author xiaoyu 2016年3月18日
 */
@Configuration
@EnableAutoConfiguration
@EnableCaching// 启用缓存
public class CacheManagerConfiguration {

	private static Logger logger = Logger.getLogger(CacheManagerConfiguration.class);
	
	@Resource(name = "ecacheManager")
	private CacheManager cacheManager;

	/**注册cachebean
	 *@author xiaoyu
	 *@param factory
	 *@return
	 *@time 2016年3月18日下午8:40:12
	 */
	@Bean(name = "ecacheManager")
	public CacheManager cacheManager(EhCacheManagerFactoryBean factory) {
		cacheManager = factory.getObject();
		logger.info("缓存管理器配置:"+cacheManager.getName());
		return cacheManager;
	}

	/**ecache bean工厂
	 *@author xiaoyu
	 *@return
	 *@time 2016年3月18日下午8:38:22
	 */
	@Bean
	public EhCacheManagerFactoryBean ehCacheManagerFactoryBean() {
		EhCacheManagerFactoryBean factory = new EhCacheManagerFactoryBean();
		factory.setCacheManagerName("EHCACHE");
		factory.setConfigLocation(new ClassPathResource("ehcache-config.xml"));
		factory.setShared(true);
		return factory;
	}

}
