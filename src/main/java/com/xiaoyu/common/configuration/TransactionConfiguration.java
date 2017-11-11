/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.common.configuration;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

/**
 * 这里用来配置不同的事务类型
 * 
 * @author xiaoyu 2016年3月17日
 */
@Configuration
@EnableAutoConfiguration
public class TransactionConfiguration implements TransactionManagementConfigurer {

    private static Logger logger = LoggerFactory.getLogger(TransactionConfiguration.class);

    @Resource(name = "manager")
    private PlatformTransactionManager manager;

    @Bean(name = "manager")
    public PlatformTransactionManager manager(DataSource dataSource) {
        // 采取默认的spring事务类型
        return new DataSourceTransactionManager(dataSource);
    }

    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        TransactionConfiguration.logger.info("事务管理:" + this.manager.getClass().getName());
        return this.manager;
    }

}
