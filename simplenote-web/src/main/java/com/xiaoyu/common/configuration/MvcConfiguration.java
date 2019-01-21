/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.common.configuration;

import java.nio.charset.Charset;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;

/**
 * 设置mvc的一些基本配置
 * 
 * @EnableWebMvc // Optionally setup Spring MVC defaults if you aren’t doing so
 *               elsewhere
 * @author xiaoyu 2016年3月21日
 *         摘自spring官方Blog:http://spring.io/blog/2013/11/01/exception
 *         -handling-in-spring-mvc
 */
@Configuration
@EnableScheduling
public class MvcConfiguration extends WebMvcConfigurerAdapter {

    /**
     * 下载设置
     * 
     * @author xiaoyu
     * @return
     * @time 2016年3月29日上午9:09:15
     */
    @Bean
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setMaxUploadSize(5_000_000);
        return multipartResolver;
    }

    /**
     * 解决@{@link responseBody}中文乱码问题
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        StringHttpMessageConverter converter1 = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        // alibaba json转化
        FastJsonHttpMessageConverter converter2 = new FastJsonHttpMessageConverter();
        converter2.setFeatures(SerializerFeature.UseISO8601DateFormat);
        converter2.setFeatures(SerializerFeature.WriteMapNullValue);
        converter2.setFeatures(SerializerFeature.WriteNullStringAsEmpty);
        converters.add(converter1);
        converters.add(converter2);
    }
}
