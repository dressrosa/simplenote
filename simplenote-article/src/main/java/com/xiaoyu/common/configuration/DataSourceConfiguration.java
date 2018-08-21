/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.common.configuration;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * @author xiaoyu 2016年3月16日 配置数据库连接池
 */
@Configuration
@MapperScan("com.xiaoyu.modules.biz.article.dao")
public class DataSourceConfiguration {

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.url}")
    private String url;

    /**
     * 和driverClassName不能同时使用
     */
    @Value("${spring.datasource.sourceClassName}")
    private String sourceClassName;

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Value("${spring.datasource.maximumPoolSize}")
    private int maximumPoolSize;

    @Value("${spring.datasource.minimumIdle}")
    private int minimumIdle;

    /**
     * @author xiaoyu
     * @return
     * @time 2016年3月16日下午3:06:25 初始化Hikari连接池
     */
    @Bean
    public DataSource initDataSource() {
        final HikariConfig config = new HikariConfig();
        config.setMaximumPoolSize(this.maximumPoolSize);
        config.setMinimumIdle(this.minimumIdle);
        // 这个会忽视JdbcUrl
        // config.setDataSourceClassName(sourceClassName);
        config.setDriverClassName(this.driverClassName);
        config.setUsername(this.username);
        config.setPassword(this.password);
        config.setJdbcUrl(this.url);
        return new HikariDataSource(config);
    }

    /**
     * @author xiaoyu
     * @return
     * @throws Exception
     * @time 2016年3月16日下午3:06:58 设置mybatis相关属性
     */
    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        final SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        // 初始化数据库
        factory.setDataSource(this.initDataSource());
        factory.setFailFast(true);
        // 设置别名包
        factory.setTypeAliasesPackage("com.xiaoyu.modules.biz.article.dao");
        final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        // 设置mapper映射路径
        factory.setMapperLocations(resolver.getResources("mappers/modules/biz/**/*Dao.xml"));
        // 获取mybatis.xml
        factory.setConfigLocation(new ClassPathResource("mybatis-config.xml"));
        return factory.getObject();
    }

}
