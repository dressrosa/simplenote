/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.common.configuration;

import javax.servlet.Filter;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 定义过滤器注册bean
 * 
 * @author xiaoyu 2016年3月24日
 */
@Configuration
@EnableAutoConfiguration
public class FilterBeanConfiguration {

	@Bean
	public FilterRegistrationBean filtersRegistration() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(encodeFilter());
		registration.addUrlPatterns("/back/*");// 只对后台接口/页面起作用
		// registration.addInitParameter("paramName", "paramValue");
		return registration;
	}

	/**
	 * 字符串过滤器
	 * 
	 * @author xiaoyu
	 * @return
	 * @time 2016年3月24日上午10:19:11
	 */
	@Bean(name = "encodeFilter")
	public Filter encodeFilter() {
		EncodeFilterConfiguration encode = new EncodeFilterConfiguration();
		return encode;
	}
}
