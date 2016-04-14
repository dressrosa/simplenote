/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */ 
package com.xiaoyu.common.configuration;

import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;

/**
 * spring官方解释: Our Spring Configuration created a Spring Bean named
 * springSessionRepositoryFilter that implements Filter. The
 * springSessionRepositoryFilter bean is responsible for replacing the
 * HttpSession with a custom implementation that is backed by Spring Session. In
 * order for our Filter to do its magic, Spring needs to load our Configuration class.
 * Last we need to ensure that our Servlet Container (i.e. Tomcat) uses our
 * springSessionRepositoryFilter for every request. Fortunately, Spring Session
 * provides a utility class named AbstractHttpSessionApplicationInitializer both
 * of these steps extremely easy. You can find an example below:
 * 
 * @author xiaoyu 2016年4月14日
 */
public class HttpSessionInitializer extends AbstractHttpSessionApplicationInitializer {

	public HttpSessionInitializer() {
		super(HttpSessionConfiguration.class);
	}
}
