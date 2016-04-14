/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */ 
package com.xiaoyu.common.configuration;

import javax.annotation.Resource;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**配置  redis spring-session
 * @author xiaoyu
 *2016年4月14日
 */
@Configuration
@EnableAutoConfiguration
@EnableRedisHttpSession
@AutoConfigureAfter(JedisPoolConfiguration.class)
public class HttpSessionConfiguration {

	/**
	 * 引入jedisPool
	 */
//	@Resource(name="jedisPool")
//	private JedisPool jedisPool;
	
	
	@Bean
	public JedisConnectionFactory connectionFactory() {
		JedisConnectionFactory factory = new JedisConnectionFactory();
		factory.setHostName("127.0.0.1");
		factory.setPort(6379);
		return factory;
	}
}
