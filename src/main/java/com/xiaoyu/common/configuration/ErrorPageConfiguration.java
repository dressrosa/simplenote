/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.common.configuration;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;

/**
 * 错误页面
 * 
 * @author xiaoyu 2016年3月21日
 */
@Controller
@EnableAutoConfiguration
public class ErrorPageConfiguration {

	@Bean
	public EmbeddedServletContainerCustomizer containerCustomizer() {

		return new EmbeddedServletContainerCustomizer() {
			@Override
			public void customize(ConfigurableEmbeddedServletContainer container) {

				// ErrorPage error401Page = new
				// ErrorPage(HttpStatus.UNAUTHORIZED, "/html/404.html");
				ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND,
						"/xiaoyu/html/404.html");
				 ErrorPage error500Page = new
				 ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/html//500.html");
				 container.addErrorPages(error500Page);
				container.addErrorPages(error404Page);
			}

		};
	}

}
