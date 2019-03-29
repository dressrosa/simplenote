/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.simplenote.common.configuration;

import javax.annotation.Resource;
import javax.sql.DataSource;

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
public class TransactionConfiguration implements TransactionManagementConfigurer {


    @Resource(name = "manager")
    private PlatformTransactionManager manager;

    @Bean(name = "manager")
    public PlatformTransactionManager manager(DataSource dataSource) {
        // 采取默认的spring事务类型
        return new DataSourceTransactionManager(dataSource);
    }

    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return this.manager;
    }

}
