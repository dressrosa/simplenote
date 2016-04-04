/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.common.configuration;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * 设置字符串过滤器 单独设置默认是对全部url起作用,设置@{@link WebFilter}范围也没有作用 需要注册bean
 * {@link FilterBeanConfiguration}
 * 
 * @author xiaoyu 2016年3月19日
 */
@Component
public class EncodeFilterConfiguration implements Filter {

	private static Logger logger = Logger
			.getLogger(EncodeFilterConfiguration.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		// System.out.println(filterConfig.getServletContext().getServletContextName());
		// System.out.println(filterConfig.getServletContext().getContextPath());
		// System.out.println(filterConfig.getServletContext().getVirtualServerName());
		// System.out.println(filterConfig.getServletContext().getServerInfo());

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		logger.info(">>>>>>>>>>>>>>>>>>>进入过滤器<<<<<<<<<<<<<<<<<<<<<<<<");
		HttpServletResponse res = (HttpServletResponse) response;
		HttpServletRequest req = (HttpServletRequest) request;
		/*
		 * stackoverflow 解释:
		 * In order to create a session, you (almost
		 * always) need to set a Session cookie. That is not possible when the
		 * response has already been committed (i.e. the HTTP headers already
		 * sent to the client). In this case, it seems that Facelets internally
		 * needs a session for something.
		 */
		req.getSession(true);
		
		res.setCharacterEncoding("utf-8");// 设置编码格式
		res.setHeader("myName", "xiaoyu");// 设置请求头
		// 对于无参请求 设置时间戳 不允许页面缓存
		req.setCharacterEncoding("utf-8");
		// Method[] m= req.getClass().getMethods();
		logger.info("请求详细信息:\n"
				// +"contentLength:"+req.getContentLength()+"\n"
				// +"contentType:"+req.getContentType()+"\n"
				+ "contentPath:"
				+ req.getContextPath()
				+ "\n"
				// /+"localAddr:"+req.getLocalAddr()+"\n"
				// +"localName:"+req.getLocalName()+"\n"
				// +"method:"+req.getMethod()+"\n"
				// +"pathInfo:"+req.getPathInfo()+"\n"
				// +"pathTranslated:"+req.getPathTranslated()+"\n"
				// +"queryString:"+req.getQueryString()+"\n"
				// +"remoteAddr:"+req.getRemoteAddr()+"\n"
				// +"remoteHost:"+req.getRemoteHost()+"\n"
				+ "requestURL:" + req.getRequestURL() + "\n"
				+ "servletContext:" + req.getServletContext() + "\n"
				+ "requestURI:" + req.getRequestURI() + "\n"
		// +"parts:"+req.getParts()+"\n"
		);
		if (0 == req.getParameterMap().size()) {
			res.sendRedirect(req.getRequestURI() + "?"
					+ System.currentTimeMillis());
			//这里直接跳转页面,就返回了 无需继续执行,继续执行等于二次请求 会报错
			//Cannot call sendError() after the response has been committed
			return;
		}
		
		
		chain.doFilter(req, res);// 请求转发

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

}
