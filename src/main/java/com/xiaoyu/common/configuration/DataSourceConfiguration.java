/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */ 
package com.xiaoyu.common.configuration;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * @author xiaoyu
 *2016年3月16日
 *配置数据库连接池
 */
@Configuration
@MapperScan("com.xiaoyu")
@EnableAutoConfiguration
public class DataSourceConfiguration {

	@Value("${spring.datasource.username}")
	private String username;//用户名
	
	@Value("${spring.datasource.password}")
	private String password;//密码
	
	@Value("${spring.datasource.url}")
	private String url;//数据库地址
	
	@Value("${spring.datasource.sourceClassName}")
	private String sourceClassName;//和driverClassName不能同时使用
	 
	@Value("${spring.datasource.driverClassName}")
	private String driverClassName;
	
	@Value("${spring.datasource.maximumPoolSize}")
	private int maximumPoolSize;
	
	@Value("${spring.datasource.minimumIdle}")
	private int minimumIdle;
	
	/**
	 *@author xiaoyu
	 *@return
	 *@time 2016年3月16日下午3:06:25
	 *初始化Hikari连接池
	 */
	@Bean
	public DataSource initDataSource() {
		HikariConfig config = new HikariConfig();
		config.setMaximumPoolSize(maximumPoolSize);
		config.setMinimumIdle(minimumIdle);
		//config.setDataSourceClassName(sourceClassName);//这个会忽视JdbcUrl
		config.setDriverClassName(driverClassName);
		config.setUsername(username);
		config.setPassword(password);
		config.setJdbcUrl(url);
		//config.setConnectionTestQuery("select 1");
		HikariDataSource hds = new HikariDataSource(config);
		return hds;
	}
	
	/**
	 *@author xiaoyu
	 *@return
	 *@throws Exception
	 *@time 2016年3月16日下午3:06:58
	 *设置mybatis相关属性
	 */
	@Bean(name="sqlSessionFactory")
	public SqlSessionFactory sqlSessionFactory() throws Exception {
		SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
		factory.setDataSource(this.initDataSource());//初始化数据库
		factory.setFailFast(true);
		factory.setTypeAliasesPackage("com.xiaoyu.modules.biz");//设置别名包
		ResourcePatternResolver  resolver = new PathMatchingResourcePatternResolver();
		factory.setMapperLocations(resolver.getResources("mappers/modules/biz/**/*Dao.xml"));//设置mapper映射路径
		factory.setConfigLocation(new ClassPathResource("mybatis-config.xml"));//获取mybatis.xml
		return factory.getObject();
	}
	
	public static void main(String args[]) {
		SpringApplication.run(DataSourceConfiguration.class, args);
	}
}
