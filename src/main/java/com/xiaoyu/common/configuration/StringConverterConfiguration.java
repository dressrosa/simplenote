/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.common.configuration;

import java.nio.charset.Charset;

import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.http.converter.StringHttpMessageConverter;

/**
 * 解决@{@link reponseBody}中文乱码 其实这里并没有起作用 ,而是设置{@link MvcConfiguration}里面起作用了<br>
 * TODO 这里暂时 也不知道为什么
 * 
 * @author xiaoyu 2016年3月23日
 */
@Deprecated
// @Configuration
public class StringConverterConfiguration {

    // private static final Charset DEFAULT_CHARSET = Charset.forName("utf-8");

    /**
     * 配置各种类型的转化器
     * 
     * @author xiaoyu
     * @return
     * @time 2016年3月23日下午6:17:12
     */
    // @Bean
    public HttpMessageConverters messageConverters() {
        final StringHttpMessageConverter converter1 = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        return new HttpMessageConverters(converter1);
    }

}
