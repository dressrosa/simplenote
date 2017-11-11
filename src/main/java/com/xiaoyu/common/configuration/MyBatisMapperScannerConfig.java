/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.common.configuration;

import org.apache.log4j.Logger;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;

/**
 * mybatis扫描接口
 * 
 * @author xiaoyu 2016年3月16日
 */
@Deprecated
// @Configuration
// @AutoConfigureAfter(DataSourceConfiguration.class)//在datasource初始化后
public class MyBatisMapperScannerConfig {

    Logger logger = Logger.getLogger(MyBatisMapperScannerConfig.class);

    @Bean
    public MapperScannerConfigurer scannerConfigurer() {
        this.logger.info("进入mybatis扫描");
        final MapperScannerConfigurer config = new MapperScannerConfigurer();
        config.setAnnotationClass(org.springframework.stereotype.Repository.class);// 指定扫描的注解接口
        config.setSqlSessionFactoryBeanName("sqlSessionFactory");
        // config.setBasePackage("com.xiaoyu");//TODO 暂时未理解
        return config;
    }

}
