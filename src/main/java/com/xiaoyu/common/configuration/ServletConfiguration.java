/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.common.configuration;


import org.springframework.boot.context.web.SpringBootServletInitializer;

/**SpringBootServletInitializer的解释:
 *  <br>Note that a WebApplicationInitializer is only
 *  needed if you are building a war file and deploying it.
 *   If you prefer to run an embedded container 
 *   then you won't need this at all.
 * @author xiaoyu 2016年3月16日
 */
@Deprecated
//@EnableAutoConfiguration
public class ServletConfiguration extends SpringBootServletInitializer {
//	@Override
//	protected SpringApplicationBuilder configure(
//			SpringApplicationBuilder builder) {
//		// TODO Auto-generated method stub
//		return builder.sources(ServletConfiguration.class);
//	}
}
