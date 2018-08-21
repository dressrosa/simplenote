/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.common.configuration;

import java.nio.charset.Charset;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

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
     * springboot 默认静态资源访问路径
     */
    private static final String[] CLASSPATH_RESOURCE_LOCATIONS = { "classpath:/META-INF/resources/",
            "classpath:/resources/", "classpath:/static/", "classpath:/public/" };

    /**
     * 前缀
     */
    @Value("${spring.mvc.view.prefix}")
    private String prefix;
    /**
     * 后缀
     */
    @Value("${spring.mvc.view.suffix}")
    private String suffix;
    /**
     * 图片目录
     */
    @Value("${img.imagesDir}")
    private String imagesDir;
    /**
     * 图片磁盘
     */
    @Value("${img.disk}")
    private String disk;

    /**
     * 基本的配置(错误页面和全局异常捕捉)
     * 
     * @author xiaoyu
     * @return
     * @time 2016年3月22日上午8:28:04
     */
    //@Bean(name = "simpleMappingExceptionResolver")
    public SimpleMappingExceptionResolver createSimpleMappingExceptionResolver() {
        final SimpleMappingExceptionResolver r = new SimpleMappingExceptionResolver();
        // Properties mappings = new Properties();
        // mappings.setProperty("DatabaseException", "databaseError");
        // mappings.setProperty("RuntimeException", "runtimeError");
        // r.setExceptionMappings(mappings); // None by default
        // 只能拦截Exception，404错误是拦截不了
        // 产生exception后跳转的页面
       // r.setDefaultErrorView("common/500");
        // Default is "exception"
        // r.setExceptionAttribute("ex");
        // No default//TODO 未明白配置
        // r.setWarnLogCategory("info");
        return r;
    }

    /**
     * 设置注解{@link EnableWebMvc} 会破坏默认的静态资源访问路径 设置静态资源访问路径
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (!registry.hasMappingForPattern("/webjars/**")) {
            registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        }
        // 将访问图片的地址映射到具体文件夹
        if (!registry.hasMappingForPattern("/images/**")) {
            registry.addResourceHandler("/images/**").addResourceLocations("file:" + this.disk + this.imagesDir);
        }
        if (!registry.hasMappingForPattern("/**")) {
            registry.addResourceHandler("/**").addResourceLocations(MvcConfiguration.CLASSPATH_RESOURCE_LOCATIONS);
        }
    }

    /**
     * 下载设置
     * 
     * @author xiaoyu
     * @return
     * @time 2016年3月29日上午9:09:15
     */
    @Bean
    public MultipartResolver multipartResolver() {
        final CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
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

    /**
     * 视图解析器
     */
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        super.configureViewResolvers(registry);
        // 设置多个视图解析器是没用的
        final InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix(this.prefix);
        resolver.setSuffix(this.suffix);
        resolver.setOrder(0);
        registry.viewResolver(resolver);

    }

    /**
     * 设置起始欢迎页
     */
    // @Override
    // public void addViewControllers(ViewControllerRegistry registry) {
    // registry.addViewController("/").setViewName("forward:/article/home.html");
    // registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    // super.addViewControllers(registry);
    // }

}
