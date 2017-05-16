/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.article.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
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
	@RequestMapping(value = "back/article/get/{articleId}", method = RequestMethod.GET)
	public String get(@PathVariable String articleId, Model model, HttpServletRequest request,
			HttpServletResponse response) {
		Article article = new Article();
		article.setId(articleId);
		Article a = this.articleService.get(article);
		model.addAttribute(a);
		return "back/article/articleDetail";
	}

	/**
	 * 首页的列表
	 * 
	 * @author xiaoyu
	 * @param model
	 * @return
	 * @time 2016年4月1日下午4:08:19
	 */
	@RequestMapping(value = "back/article/homePageList", method = RequestMethod.GET)
	public String homePageList(Model model, HttpServletRequest request, HttpServletResponse response) {
		if (EhCacheUtil.IsExist("SystemCache")) {// 从缓存取
			@SuppressWarnings("unchecked")
			List<Object> total = (List<Object>) EhCacheUtil.get("SystemCache", "pageList");
			model.addAttribute("list", total);
			if (total != null && total.size() > 0) {
				return "back/article/articleList";
			}
		}
		Page<Article> page = this.articleService.findByPage(new Article(), 1, 12);
		List<Article> list = page.getResult();
		for (Article a : list) {
			User u = new User();
			a.setContent(a.getContent().substring(0, 250));
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
		return "back/article/articleList";
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
	@RequestMapping(value = "back/article/list", method = RequestMethod.GET)
	public String list(@ModelAttribute Article article, Model model, Integer pageNum, Integer pageSize) {
		Page<Article> page = this.articleService.findByPage(article, pageNum, pageSize);
		List<Article> list = page.getResult();
		User u = new User();
		for (Article a : list) {
			a.setContent(a.getContent().substring(0, 250));
			u.setId(a.getUserId());
			u = this.userService.get(u);
			a.setUser(u);
		}
		model.addAttribute("list", list);
		return "back/article/articleList";
	}

	@RequestMapping(value = "back/article/changeView", method = RequestMethod.POST)
	@ResponseBody
	public String changeView(String id) {
		ArticleAttr attr = new ArticleAttr();
		attr.setArticleId(id);
		int total = this.articleAttrService.updateReadNum(attr);
		return total + "";
	}

	@RequestMapping(value = "addArticle")
	public String addArticle(HttpServletResponse response, HttpServletRequest request) {
		System.out.println("缓存时间:" + request.getSession().getMaxInactiveInterval());
		if (request.getSession().getAttributeNames().hasMoreElements()) {
			System.out.println(request.getSession().getAttributeNames().nextElement());
		}
		return "back/article/articleForm";
	}
}