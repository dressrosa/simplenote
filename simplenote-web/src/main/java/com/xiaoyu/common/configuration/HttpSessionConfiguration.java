/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.common.configuration;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 配置 redis spring-session
 * 
 * @author xiaoyu 2016年4月14日
 */
@Configuration
@EnableRedisHttpSession
@AutoConfigureAfter(JedisPoolConfiguration.class)
public class HttpSessionConfiguration {

}
