/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.article.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import com.xiaoyu.common.base.ResponseMapper;
import com.xiaoyu.common.base.ResultConstant;
import com.xiaoyu.common.utils.EhCacheUtil;
import com.xiaoyu.modules.biz.article.entity.Article;
import com.xiaoyu.modules.biz.article.entity.ArticleAttr;
import com.xiaoyu.modules.biz.article.service.ArticleAttrService;
import com.xiaoyu.modules.biz.article.service.ArticleService;
import com.xiaoyu.modules.biz.user.entity.User;
import com.xiaoyu.modules.biz.user.service.UserService;

/**
 * 文章
 * 
 * @author xiaoyu 2016年3月29日
 */
@Controller
public class ArticleBackController {

	@Autowired
	private ArticleService articleService;

	@Autowired
	private UserService userService;

	@Autowired
	private ArticleAttrService articleAttrService;

	private Map<String, Object> article2Map(Article a) {
		Map<String, Object> map = new HashMap<>();
		map.put("articleId", a.getId());
		map.put("content", a.getContent().length() > 150 ? a.getContent().substring(0, 149) : a.getContent());
		map.put("title", a.getTitle());
		return map;
	}

	/**
	 * 获取文章详情
	 * 
	 * @author xiaoyu
	 * @param article
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @time 2016年3月29日下午9:20:30
	 */
	@RequestMapping(value = "public/article/{articleId}", method = RequestMethod.GET)
	public String get(@PathVariable String articleId, Model model, HttpServletRequest request,
			HttpServletResponse response) {
		Article article = new Article();
		article.setId(articleId);
		Article a = this.articleService.get(article);
		if (a == null) {
			return "common/404";
		}
		model.addAttribute(a);
		return "article/articleDetail";
	}

	/**
	 * 首页的列表
	 * 
	 * @author xiaoyu
	 * @param model
	 * @return
	 * @time 2016年4月1日下午4:08:19
	 */
	@RequestMapping(value = "public/article/hot", method = RequestMethod.GET)
	public String hotList(Model model, HttpServletRequest request, HttpServletResponse response) {
		if (EhCacheUtil.IsExist("SystemCache")) {// 从缓存取
			@SuppressWarnings("unchecked")
			List<Object> total = (List<Object>) EhCacheUtil.get("SystemCache", "pageList");
			model.addAttribute("list", total);
			if (total != null && total.size() > 0) {
				return "article/articleList";
			}
		}
		Page<Article> page = this.articleService.findByPage(new Article(), 1, 12);
		List<Article> list = page.getResult();
		for (Article a : list) {
			User u = new User();
			a.setContent(a.getContent().length() > 100 ? a.getContent().substring(0, 99) : a.getContent());
			u.setId(a.getUserId());
			u = this.userService.get(u);
			a.setUser(u);
		}
		List<Article> childList1 = Lists.newArrayList();
		List<Article> childList2 = Lists.newArrayList();
		List<Article> childList3 = Lists.newArrayList();
		List<Article> childList4 = Lists.newArrayList();
		List<Object> total = Lists.newArrayList();
		for (int i = 0; i < list.size(); i++) {
			if (i < 3) {
				childList1.add(list.get(i));
			} else if (i > 2 && i < 6) {
				childList2.add(list.get(i));
			} else if (i > 5 && i < 9) {
				childList3.add(list.get(i));
			} else {
				childList4.add(list.get(i));
			}
		}
		total.add(childList1);
		total.add(childList2);
		total.add(childList3);
		total.add(childList4);
		EhCacheUtil.put("SystemCache", "pageList", total);// 存入缓存
		model.addAttribute("list", total);
		return "article/articleList";
	}

	/**
	 * 文章列表
	 * 
	 * @author xiaoyu
	 * @param article
	 * @param model
	 * @param pageNum
	 * @param pageSize
	 * @return
	 * @time 2016年3月30日下午2:16:59
	 */
	@RequestMapping(value = "public/article/list", method = RequestMethod.POST)
	@ResponseBody
	public String list(HttpServletRequest request, String userId, Integer pageNum, Integer pageSize) {
		ResponseMapper mapper = ResponseMapper.createMapper();
		Article article = new Article();
		article.setUserId(userId);
		if (pageNum == null || pageSize == null || pageNum < 0 || pageSize < 0) {
			pageNum = 0;
			pageSize = 10;
		}
		Page<Article> page = this.articleService.findByPage(article, pageNum, pageSize);
		List<Article> list = page.getResult();
		List<Map<String, Object>> total = new ArrayList<>();
		if (list != null && list.size() > 0) {
			for (Article a : list) {
				total.add(this.article2Map(a));
			}
		}
		return mapper.setData(total).getResultJson();
	}

	@RequestMapping(value = "public/article/changeView/{articleId}", method = RequestMethod.POST)
	@ResponseBody
	public String changeView(HttpServletRequest requset, @PathVariable String id) {
		ArticleAttr attr = new ArticleAttr();
		attr.setArticleId(id);
		int total = this.articleAttrService.updateReadNum(attr);
		return total + "";
	}

	@RequestMapping(value = "private/article/add", method = RequestMethod.POST)
	@ResponseBody
	public String addArticle(HttpServletRequest request, @RequestParam(required = true) String content,
			@RequestParam(required = true) String userId, @RequestParam(required = true) String token) {
		ResponseMapper mapper = ResponseMapper.createMapper();
		HttpSession session = request.getSession(false);
		if (session == null)
			return mapper.setCode(ResultConstant.LOGIN_INVALIDATE).setMessage("登录失效,请刷新登录1").getResultJson();
		User user = (User) session.getAttribute(token);
		if (user == null)
			return mapper.setCode(ResultConstant.LOGIN_INVALIDATE).setMessage("登录失效,请刷新登录2").getResultJson();
		if (!userId.equals(user.getId()))
			return mapper.setCode(ResultConstant.LOGIN_INVALIDATE).setMessage("登录失效,请刷新登录3").getResultJson();
		String articleId = this.articleService.addArticle(userId, content);
		return mapper.setData(articleId).getResultJson();
	}

	@RequestMapping(value = "public/article/write")
	public String write(HttpServletResponse response, HttpServletRequest request) {
		System.out.println("缓存时间:" + request.getSession().getMaxInactiveInterval());
		if (request.getSession().getAttributeNames().hasMoreElements()) {
			System.out.println(request.getSession().getAttributeNames().nextElement());
		}
		return "article/articleForm";
	}
}