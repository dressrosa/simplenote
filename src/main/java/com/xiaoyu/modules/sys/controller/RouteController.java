package com.xiaoyu.modules.sys.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.xiaoyu.common.utils.StringUtils;

/**
 * 2017年5月24日上午9:59:18
 * 
 * @author xiaoyu
 * @description 只负责地址跳转
 */
@Controller
public class RouteController {

	@RequestMapping(value = "article/hot", method = RequestMethod.GET)
	public String hotList(HttpServletRequest request, HttpServletResponse response) {
		return "article/hotList";
	}

	@RequestMapping(value = "article/{articleId}", method = RequestMethod.GET)
	public String goArticleDetail(HttpServletRequest request, HttpServletResponse response,
			@PathVariable String articleId) {
		if (StringUtils.isBlank(articleId)) {
			return "common/404";
		}
		return "article/articleDetail";
	}

	@RequestMapping(value = "user/{userId}", method = RequestMethod.GET)
	public String goUserDetail(@PathVariable String userId, HttpServletRequest request) {
		if (StringUtils.isBlank(userId)) {
			return "common/404";
		}
		return "user/userDetail";
	}

	@RequestMapping(value = "article/write", method = RequestMethod.GET)
	public String goWrite(HttpServletRequest request) {
		return "article/articleForm";
	}

}
