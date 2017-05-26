/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.article.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import com.xiaoyu.common.base.BaseService;
import com.xiaoyu.common.base.ResponseMapper;
import com.xiaoyu.common.base.ResultConstant;
import com.xiaoyu.common.utils.EhCacheUtil;
import com.xiaoyu.common.utils.IdGenerator;
import com.xiaoyu.common.utils.JedisUtils;
import com.xiaoyu.common.utils.TimeUtils;
import com.xiaoyu.modules.biz.article.dao.ArticleAttrDao;
import com.xiaoyu.modules.biz.article.dao.ArticleDao;
import com.xiaoyu.modules.biz.article.entity.Article;
import com.xiaoyu.modules.biz.article.entity.ArticleAttr;
import com.xiaoyu.modules.biz.article.service.api.IArticleService;
import com.xiaoyu.modules.biz.user.dao.UserDao;
import com.xiaoyu.modules.biz.user.entity.User;
import com.xiaoyu.modules.sys.constant.PageUrl;

/**
 * @author xiaoyu 2016年3月29日
 */
@Service
@Transactional
public class ArticleService extends BaseService<ArticleDao, Article> implements IArticleService {

	@Autowired
	private UserDao userDao;
	@Autowired
	private ArticleDao articleDao;
	@Autowired
	private ArticleAttrDao attrDao;

	private Map<String, Object> article2Map(Article a) {
		Map<String, Object> map = new HashMap<>();
		map.put("articleId", a.getId());
		map.put("content", a.getContent());
		map.put("createDate", TimeUtils.format(a.getCreateDate(), "yyyy-MM-dd"));
		map.put("createTime", TimeUtils.format(a.getCreateDate(), "HH:mm"));
		map.put("readNum", a.getReadNum());
		map.put("title", a.getTitle());
		map.put("user", this.user2Map(this.userDao.getById(a.getUserId())));
		return map;
	}

	private Map<String, Object> article2Map1(Article a) {
		Map<String, Object> map = new HashMap<>();
		map.put("articleId", a.getId());
		map.put("content", a.getContent().length() > 200 ? a.getContent().substring(0, 199) : a.getContent());
		map.put("title", a.getTitle());
		map.put("user", this.user2Map(this.userDao.getById(a.getUserId())));
		return map;
	}

	private Map<String, Object> article2Map2(Article a) {
		Map<String, Object> map = new HashMap<>();
		map.put("articleId", a.getId());
		map.put("content", a.getContent().length() > 100 ? a.getContent().substring(0, 99) : a.getContent());
		map.put("title", a.getTitle());
		map.put("user", this.user2Map(this.userDao.getById(a.getUserId())));
		return map;
	}

	private Map<String, Object> user2Map(User a) {
		Map<String, Object> map = new HashMap<>();
		if (a != null) {
			map.put("userId", a.getId());
			map.put("nickname", a.getNickname());
			map.put("avatar", a.getAvatar());
			map.put("description", a.getDescription());
		}
		return map;
	}

	@Override
	public String detail(String articleId) {
		ResponseMapper mapper = ResponseMapper.createMapper();
		Article a = super.get(articleId);
		if (a == null) {
			return mapper.setCode(ResultConstant.NOT_DATA).setData(PageUrl.Not_Found).getResultJson();
		}
		return mapper.setData(this.article2Map(a)).getResultJson();
	}

	@Transactional(readOnly = false)
	private String publish(String userId, String content) {
		ResponseMapper mapper = ResponseMapper.createMapper();
		Article t = new Article();
		ArticleAttr attr = new ArticleAttr();
		t.setId(IdGenerator.uuid());
		t.setContent(content);
		t.setUserId(userId);
		try {
			this.articleDao.insert(t);
			attr.setArticleId(t.getId());
			attr.setId(IdGenerator.uuid());
			this.attrDao.insert(attr);
		} catch (RuntimeException e) {
			throw e;
		}
		return mapper.setData(t.getId()).getResultJson();
	}

	@Override
	public String hotList() {
		ResponseMapper mapper = ResponseMapper.createMapper();

		if (EhCacheUtil.IsExist("SystemCache")) {// 从缓存取
			@SuppressWarnings("unchecked")
			List<Object> total = (List<Object>) EhCacheUtil.get("SystemCache", "pageList");

			// model.addAttribute("list", total);
			if (total != null && total.size() > 0) {
				return mapper.setData(total).getResultJson();
				// return "article/articleList";
			}
		}
		Page<Article> page = this.findByPage(new Article(), 1, 12);
		List<Article> list = page.getResult();
		List<Object> total = new ArrayList<>();

		List<Map<String, Object>> childList1 = Lists.newArrayList();
		List<Map<String, Object>> childList2 = Lists.newArrayList();
		List<Map<String, Object>> childList3 = Lists.newArrayList();
		List<Map<String, Object>> childList4 = Lists.newArrayList();

		for (int i = 0; i < list.size(); i++) {
			if (i < 3) {
				childList1.add(this.article2Map2(list.get(i)));
			} else if (i > 2 && i < 6) {
				childList2.add(this.article2Map2(list.get(i)));
			} else if (i > 5 && i < 9) {
				childList3.add(this.article2Map2(list.get(i)));
			} else {
				childList4.add(this.article2Map2(list.get(i)));
			}
		}
		total.add(childList1);
		total.add(childList2);
		total.add(childList3);
		total.add(childList4);
		EhCacheUtil.put("SystemCache", "pageList", total);// 存入缓存
		// model.addAttribute("list", total);
		// return "article/articleList";
		return mapper.setData(total).getResultJson();
	}

	@Override
	public String list(HttpServletRequest request, String userId, Integer pageNum, Integer pageSize) {
		ResponseMapper mapper = ResponseMapper.createMapper();
		Article article = new Article();
		article.setUserId(userId);
		if (pageNum == null || pageSize == null || pageNum < 0 || pageSize < 0) {
			pageNum = 0;
			pageSize = 10;
		}
		Page<Article> page = this.findByPage(article, pageNum, pageSize);
		List<Article> list = page.getResult();
		List<Map<String, Object>> total = new ArrayList<>();
		if (list != null && list.size() > 0) {
			for (Article a : list) {
				total.add(this.article2Map1(a));
			}
		}
		return mapper.setData(total).getResultJson();
	}

	@Override
	public String addArticle(HttpServletRequest request, String content, String userId, String token) {
		ResponseMapper mapper = ResponseMapper.createMapper();
		HttpSession session = request.getSession(false);
		if (session == null)
			return mapper.setCode(ResultConstant.LOGIN_INVALIDATE).setMessage("登录失效,请刷新登录1").getResultJson();
		User user = (User) session.getAttribute(token);
		if (user == null)
			return mapper.setCode(ResultConstant.LOGIN_INVALIDATE).setMessage("登录失效,请刷新登录2").getResultJson();
		if (!userId.equals(user.getId()))
			return mapper.setCode(ResultConstant.LOGIN_INVALIDATE).setMessage("登录失效,请刷新登录3").getResultJson();
		return this.publish(userId, content);
	}

	@Override
	public String addReadNum(HttpServletRequest request, String articleId) {
		String ip = request.getRemoteHost();
		if (JedisUtils.get("user:login:" + ip) != null) {
			return ResponseMapper.createMapper().getResultJson();
		}
		ArticleAttr attr = new ArticleAttr();
		attr.setArticleId(articleId);
		attr = this.attrDao.getForUpdate(attr);// 行级锁
		ArticleAttr temp = new ArticleAttr();
		temp.setId(attr.getId());
		temp.setArticleId(attr.getArticleId());
		temp.setReadNum(attr.getReadNum() + 1);
		this.attrDao.update(temp);
		JedisUtils.set("user:login:" + ip, temp.getReadNum().toString(), 60 * 10);
		return ResponseMapper.createMapper().setData(temp.getReadNum()).getResultJson();
	}
}
