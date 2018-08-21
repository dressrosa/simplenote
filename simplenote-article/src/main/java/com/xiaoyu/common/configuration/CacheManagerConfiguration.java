/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.common.configuration;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import net.sf.ehcache.CacheManager;

/**
 * 设置ehcache为默认cachemanager
 * 
 * @author xiaoyu 2016年3月18日
 */
@Configuration
@EnableCaching 
public class CacheManagerConfiguration {

    private final static Logger logger = LoggerFactory.getLogger(CacheManagerConfiguration.class);

    @Resource(name = "ecacheManager")
    private CacheManager cacheManager;

    /**
     * 注册cachebean
     * 
     * @author xiaoyu
     * @param factory
     * @return
     * @time 2016年3月18日下午8:40:12
     */
    @Bean(name = "ecacheManager")
    public CacheManager cacheManager(EhCacheManagerFactoryBean factory) {
        this.cacheManager = factory.getObject();
        CacheManagerConfiguration.logger.info("缓存管理器配置:" + this.cacheManager.getName());
        return this.cacheManager;
    }

    /**
     * ecache bean工厂
     * 
     * @author xiaoyu
     * @return
     * @time 2016年3月18日下午8:38:22
     */
    @Bean
    public EhCacheManagerFactoryBean ehCacheManagerFactoryBean() {
        final EhCacheManagerFactoryBean factory = new EhCacheManagerFactoryBean();
        factory.setCacheManagerName("EHCACHE");
        factory.setConfigLocation(new ClassPathResource("ehcache-config.xml"));
        factory.setShared(true);
        return factory;
    }

}
