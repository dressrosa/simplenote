/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.article.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xiaoyu.common.base.BaseService;
import com.xiaoyu.common.base.ResponseMapper;
import com.xiaoyu.common.base.ResultConstant;
import com.xiaoyu.common.utils.ElasticUtils;
import com.xiaoyu.common.utils.IdGenerator;
import com.xiaoyu.common.utils.JedisUtils;
import com.xiaoyu.common.utils.StringUtils;
import com.xiaoyu.common.utils.TimeUtils;
import com.xiaoyu.modules.biz.article.dao.ArticleAttrDao;
import com.xiaoyu.modules.biz.article.dao.ArticleCollectDao;
import com.xiaoyu.modules.biz.article.dao.ArticleCommentDao;
import com.xiaoyu.modules.biz.article.dao.ArticleDao;
import com.xiaoyu.modules.biz.article.dao.ArticleLikeDao;
import com.xiaoyu.modules.biz.article.entity.Article;
import com.xiaoyu.modules.biz.article.entity.ArticleAttr;
import com.xiaoyu.modules.biz.article.entity.ArticleCollect;
import com.xiaoyu.modules.biz.article.entity.ArticleComment;
import com.xiaoyu.modules.biz.article.entity.ArticleLike;
import com.xiaoyu.modules.biz.article.entity.CommentLike;
import com.xiaoyu.modules.biz.article.service.api.IArticleService;
import com.xiaoyu.modules.biz.article.vo.ArticleCommentVo;
import com.xiaoyu.modules.biz.article.vo.ArticleVo;
import com.xiaoyu.modules.biz.user.dao.UserAttrDao;
import com.xiaoyu.modules.biz.user.dao.UserDao;
import com.xiaoyu.modules.biz.user.entity.User;
import com.xiaoyu.modules.biz.user.vo.UserVo;
import com.xiaoyu.modules.sys.constant.NumCountType;

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
	@Autowired
	private ArticleCollectDao collectDao;
	@Autowired
	private UserAttrDao userAttrDao;

	/**
	 * 检查登录失效
	 */
	private User checkLoginDead(HttpServletRequest request) {
		String userId = request.getHeader("userId");
		String token = request.getHeader("token");
		HttpSession session = request.getSession(false);
		if (session == null)
			return null;
		User user = (User) session.getAttribute(token);
		if (user == null)
			return null;
		if (!userId.equals(user.getId()))
			return null;
		return user;
	}

	private Map<String, Object> article2Map(ArticleVo a) {
		Map<String, Object> map = new HashMap<>();
		map.put("articleId", a.getId());
		map.put("content", a.getContent());
		map.put("createDate", TimeUtils.format(a.getCreateDate(), "yyyy-MM-dd"));
		map.put("createTime", TimeUtils.format(a.getCreateDate(), "HH:mm"));
		map.put("title", a.getTitle());
		map.put("user", this.user2Map(this.userDao.getVoById(a.getUserId())));
		map.put("attr", a.getAttr());
		return map;
	}

	private Map<String, Object> article2Map1(ArticleVo a) {
		Map<String, Object> map = new HashMap<>();
		map.put("articleId", a.getId());
		map.put("content", a.getContent().length() > 200 ? a.getContent().substring(0, 199) : a.getContent());
		map.put("title", a.getTitle());
		map.put("user", this.user2Map(this.userDao.getVoById(a.getUserId())));
		map.put("attr", a.getAttr());
		map.put("isLike", "0");
		map.put("isCollect", "0");
		return map;
	}

	private Map<String, Object> user2Map(UserVo a) {
		Map<String, Object> map = new HashMap<>();
		if (a != null) {
			map.put("userId", a.getUserId());
			map.put("nickname", a.getNickname());
			map.put("avatar", a.getAvatar());
			map.put("signature", a.getSignature());
			map.put("description", a.getDescription());
			map.put("attr", a.getAttr());
		}
		return map;
	}

	@Override
	public String detail(String articleId) {
		ResponseMapper mapper = ResponseMapper.createMapper();
		ArticleVo a = this.articleDao.getVo(articleId);
		if (a == null) {
			return mapper.setCode(ResultConstant.NOT_DATA).getResultJson();
		}
		return mapper.setData(this.article2Map(a)).getResultJson();
	}

	@Transactional(readOnly = false)
	private String publish(String userId, String title, String content) {
		ResponseMapper mapper = ResponseMapper.createMapper();
		Article t = new Article();
		ArticleAttr attr = new ArticleAttr();
		t.setId(IdGenerator.uuid());
		t.setContent(content).setTitle(title).setUserId(userId);
		try {
			this.articleDao.insert(t);
			attr.setArticleId(t.getId());
			attr.setId(IdGenerator.uuid());
			this.attrDao.insert(attr);
			try {// 增加发文数
				this.userAttrDao.addNum(NumCountType.ArticleNum.ordinal(), 1, userId);
			} catch (Exception e) {
				// do nothing
			}
		} catch (RuntimeException e) {
			throw e;
		}
		return mapper.setData(t.getId()).getResultJson();
	}

	@Override
	public String hotList(HttpServletRequest request) {
		ResponseMapper mapper = ResponseMapper.createMapper();
		System.out.println(request.getHeader("userId"));
		// if (EhCacheUtil.IsExist("SystemCache")) {// 从缓存取
		// @SuppressWarnings("unchecked")
		// List<Object> total = (List<Object>) EhCacheUtil.get("SystemCache",
		// "pageList");
		// if (total != null && total.size() > 0) {
		// return mapper.setData(total).getResultJson();
		// }
		// }
		PageHelper.startPage(1, 12);
		Page<ArticleVo> page = (Page<ArticleVo>) this.articleDao.findHotList();
		List<ArticleVo> list = page.getResult();
		List<Object> total = new ArrayList<>();

		boolean isLogin = (checkLoginDead(request) != null);// 是否登录
		ArticleLike t = new ArticleLike();
		t.setUserId(request.getHeader("userId"));
		ArticleCollect t1 = new ArticleCollect();
		t1.setUserId(request.getHeader("userId"));

		for (ArticleVo a : list) {
			Map<String, Object> m = this.article2Map1(a);
			if (isLogin) {
				t.setArticleId(a.getId());
				ArticleLike al = this.likeDao.get(t);
				if (al != null) {
					m.put("isLike", al.getStatus() + "");
				}
				t1.setArticleId(a.getId());
				ArticleCollect ac = this.collectDao.get(t1);
				if (ac != null) {
					m.put("isCollect", ac.getStatus() + "");
				}

			}
			total.add(m);
		}
		// EhCacheUtil.put("SystemCache", "pageList", total);// 存入缓存
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

		Page<ArticleVo> page = this.findByPageWithAttr(userId, pageNum, pageSize);
		List<ArticleVo> list = page.getResult();
		List<Map<String, Object>> total = new ArrayList<>();

		boolean isLogin = (checkLoginDead(request) != null);// 是否登录
		ArticleLike t = new ArticleLike();
		t.setUserId(request.getHeader("userId"));
		ArticleCollect t1 = new ArticleCollect();
		t1.setUserId(request.getHeader("userId"));

		if (list != null && list.size() > 0) {
			for (ArticleVo a : list) {
				Map<String, Object> m = this.article2Map1(a);
				if (isLogin) {
					t.setArticleId(a.getId());
					ArticleLike al = this.likeDao.get(t);
					if (al != null) {
						m.put("isLike", al.getStatus() + "");
					}
					t1.setArticleId(a.getId());
					ArticleCollect ac = this.collectDao.get(t1);
					if (ac != null) {
						m.put("isCollect", ac.getStatus() + "");
					}

				}
				total.add(m);
			}
		}
		return mapper.setData(total).getResultJson();
	}

	@Override
	public String collectList(HttpServletRequest request, String userId, Integer pageNum, Integer pageSize) {
		ResponseMapper mapper = ResponseMapper.createMapper();
		boolean isLogin = (checkLoginDead(request) != null);// 是否登录
		// if (!isLogin) {
		// return
		// mapper.setCode(ResultConstant.LOGIN_INVALIDATE).setMessage(ResultConstant.LOGIN_INVALIDATE_MESSAGE)
		// .getResultJson();
		// }
		// if (!userId.equals(request.getHeader("userId"))) {
		// return
		// mapper.setCode(ResultConstant.REQ_NOACCESS).setMessage(ResultConstant.REQ_NOACCESS_MESSAGE)
		// .getResultJson();
		// }
		Article article = new Article();
		article.setUserId(userId);
		if (pageNum == null || pageSize == null || pageNum < 0 || pageSize < 0) {
			pageNum = 0;
			pageSize = 10;
		}

		Page<ArticleVo> page = new Page<ArticleVo>();
		PageHelper.startPage(pageNum, pageSize);
		page = (Page<ArticleVo>) this.articleDao.findCollectList(userId);
		List<ArticleVo> list = page.getResult();
		List<Map<String, Object>> total = new ArrayList<>();

		ArticleLike t = new ArticleLike();
		t.setUserId(request.getHeader("userId"));
		ArticleCollect t1 = new ArticleCollect();
		t1.setUserId(request.getHeader("userId"));

		if (list != null && list.size() > 0) {
			for (ArticleVo a : list) {
				Map<String, Object> m = this.article2Map1(a);
				Map<String, Object> map = new HashMap<>();
				map.put("articleId", a.getId());
				map.put("title", a.getTitle());
				map.put("user", this.user2Map(this.userDao.getVoById(a.getUserId())));
				map.put("attr", a.getAttr());
				map.put("isLike", "0");
				map.put("isCollect", "0");

				if (isLogin) {
					t.setArticleId(a.getId());
					ArticleLike al = this.likeDao.get(t);
					if (al != null) {
						m.put("isLike", al.getStatus() + "");
					}
					t1.setArticleId(a.getId());
					ArticleCollect ac = this.collectDao.get(t1);
					if (ac != null) {
						m.put("isCollect", ac.getStatus() + "");
					}

				}
				total.add(m);
			}
		}
		return mapper.setData(total).getResultJson();
	}

	private Page<ArticleVo> findByPageWithAttr(String userId, int pageNum, int pageSize) {
		Page<ArticleVo> page = new Page<ArticleVo>();
		PageHelper.startPage(pageNum, pageSize);
		page = (Page<ArticleVo>) this.articleDao.findByListWithAttr(userId);
		return page;
	}

	@Override
	public String addArticle(HttpServletRequest request, String title, String content, String userId, String token) {
		ResponseMapper mapper = ResponseMapper.createMapper();
		HttpSession session = request.getSession(false);
		if (session == null)
			return mapper.setCode(ResultConstant.LOGIN_INVALIDATE).setMessage("登录失效,请刷新登录1").getResultJson();
		User user = (User) session.getAttribute(token);
		if (user == null)
			return mapper.setCode(ResultConstant.LOGIN_INVALIDATE).setMessage("登录失效,请刷新登录2").getResultJson();
		if (!userId.equals(user.getId()))
			return mapper.setCode(ResultConstant.LOGIN_INVALIDATE).setMessage("登录失效,请刷新登录3").getResultJson();
		return this.publish(userId, title, content);
	}

	@Override
	public String addReadNum(HttpServletRequest request, String articleId) {
		String ip = request.getRemoteHost();
		if (JedisUtils.get("user:login:" + ip) != null) {
			return ResponseMapper.createMapper().getResultJson();
		}
		ArticleAttr attr = this.attrDao.getForUpdate(articleId);// 行级锁
		ArticleAttr temp = new ArticleAttr();
		temp.setId(attr.getId());
		temp.setArticleId(attr.getArticleId());
		temp.setReadNum(attr.getReadNum() + 1);
		this.attrDao.update(temp);
		JedisUtils.set("user:login:" + ip, temp.getReadNum().toString(), 60 * 10);
		return ResponseMapper.createMapper().setData(temp.getReadNum()).getResultJson();
	}

	@Autowired
	private ArticleLikeDao likeDao;

	@Override
	public String addLike(HttpServletRequest request, String articleId, Integer isLike) {
		ResponseMapper mapper = ResponseMapper.createMapper();
		if (this.articleDao.isExist(articleId) < 1) {
			return mapper.setCode(ResultConstant.ARGS_ERROR).getResultJson();
		}
		if (checkLoginDead(request) == null) {// 没登录 或失效
			if (isLike == 0)
				this.addLikeNum(articleId, true);
			else if (isLike == 1)
				this.addLikeNum(articleId, false);
			return mapper.setCode(ResultConstant.LOGIN_INVALIDATE).getResultJson();
		} else {
			ArticleLike t = new ArticleLike();
			t.setUserId(request.getHeader("userId")).setArticleId(articleId);
			if (this.likeDao.isExist(t) > 0) {// 已经点过
				ArticleLike like = this.likeDao.getForUpdate(t);
				if (like.getStatus() == 1) {// 取消点赞
					t.setStatus(0);
					this.addLikeNum(articleId, false);
				} else {// 进行点赞
					t.setNum(like.getNum() + 1).setStatus(1);
					this.addLikeNum(articleId, true);
				}
				this.likeDao.update(t);
			} else {// 没点过赞
				t.setNum(1);
				this.likeDao.insert(t);
				this.addLikeNum(articleId, true);
			}

		}
		return mapper.getResultJson();

	}

	private void addLikeNum(String articleId, boolean flag) {
		ArticleAttr attr = new ArticleAttr();
		ArticleAttr at = this.attrDao.getForUpdate(articleId);
		attr.setArticleId(articleId);
		if (flag) {// 点赞
			attr.setLikeNum(at.getLikeNum() + 1);
		} else {
			attr.setLikeNum(at.getLikeNum() - 1);
		}
		this.attrDao.update(attr);
	}

	/*
	 * isCollect 0取消收藏 1收藏
	 */
	@Override
	public String addCollect(HttpServletRequest request, String articleId, Integer isCollect) {
		ResponseMapper mapper = ResponseMapper.createMapper();
		if (this.articleDao.isExist(articleId) < 1) {
			return mapper.setCode(ResultConstant.ARGS_ERROR).getResultJson();
		}
		if (checkLoginDead(request) == null) {// 没登录 或失效
			if (isCollect == 0)
				this.addCollectNum(articleId, request.getHeader("userId"), true);
			else if (isCollect == 1)
				this.addCollectNum(articleId, request.getHeader("userId"), false);
			return mapper.setCode(ResultConstant.LOGIN_INVALIDATE).getResultJson();
		} else {
			ArticleCollect t = new ArticleCollect();
			t.setUserId(request.getHeader("userId")).setArticleId(articleId);
			if (this.collectDao.isExist(t) > 0) {// 已经收藏
				ArticleCollect co = this.collectDao.getForUpdate(t);
				if (co.getStatus() == 1) {// 取消收藏
					t.setStatus(0);
					this.addCollectNum(articleId, request.getHeader("userId"), false);

				} else {// 进行收藏
					t.setStatus(1);
					this.addCollectNum(articleId, request.getHeader("userId"), true);

				}
				this.collectDao.update(t);
			} else {// 没收藏
				t.setId(IdGenerator.uuid());
				this.collectDao.insert(t);
				this.addCollectNum(articleId, request.getHeader("userId"), true);

			}

		}
		return mapper.getResultJson();

	}

	private void addCollectNum(String articleId, String userId, boolean flag) {
		ArticleAttr attr = new ArticleAttr();
		ArticleAttr at = this.attrDao.getForUpdate(articleId);
		attr.setArticleId(articleId);
		if (flag) {// 收藏
			attr.setCollectNum(at.getCollectNum() + 1);
			try {
				this.userAttrDao.addNum(NumCountType.CollectNum.ordinal(), 1, userId);
			} catch (Exception e) {
				// do nothing
			}
		} else {
			attr.setCollectNum(at.getCollectNum() - 1);
			try {
				this.userAttrDao.addNum(NumCountType.CollectNum.ordinal(), -1, userId);
			} catch (Exception e) {
				// do nothing
			}
		}
		this.attrDao.update(attr);
	}

	@Autowired
	private ArticleCommentDao arCommentDao;

	@Override
	public String comment(HttpServletRequest request, String articleId, String content) {
		ResponseMapper mapper = ResponseMapper.createMapper();
		if (StringUtils.isBlank(content)) {
			return mapper.setCode(ResultConstant.ARGS_ERROR).getResultJson();
		}
		User user = checkLoginDead(request);
		if (user == null)
			return mapper.setCode(ResultConstant.LOGIN_INVALIDATE).getResultJson();
		ArticleComment co = new ArticleComment();
		Article ar = this.articleDao.getById(articleId);
		if (ar == null)
			return mapper.setCode(ResultConstant.ARGS_ERROR).getResultJson();
		co.setArticleId(articleId).setReplyerId(user.getId()).setContent(content).setAuthorId(ar.getUserId())
				.setId(IdGenerator.uuid());
		if (this.arCommentDao.insert(co) > 0) {
			this.addCommentNum(articleId, true);
		}
		Map<String, String> map = new HashMap<>();
		map.put("replyerId", user.getId());
		map.put("replyerName", user.getNickname());
		map.put("replyerAvatar", user.getAvatar());
		map.put("content", content);
		map.put("createDate", TimeUtils.format(new Date(), "yyyy-MM-dd HH:mm"));
		return mapper.setData(map).getResultJson();
	}

	private void addCommentNum(String articleId, boolean flag) {
		ArticleAttr attr = new ArticleAttr();
		ArticleAttr at = this.attrDao.getForUpdate(articleId);
		attr.setArticleId(articleId);
		if (flag) {// 评论
			attr.setCommentNum(at.getCommentNum() + 1);
		} else {// 删除评论
			attr.setCommentNum(at.getCommentNum() - 1);
		}
		this.attrDao.update(attr);
	}

	@Override
	public String comments(HttpServletRequest request, String articleId, Integer pageNum) {
		ResponseMapper mapper = ResponseMapper.createMapper();
		PageHelper.startPage(pageNum, 10);
		Page<ArticleCommentVo> page = (Page<ArticleCommentVo>) this.arCommentDao.findList(articleId);
		List<ArticleCommentVo> list = page.getResult();
		if (list == null || list.size() < 1)
			return mapper.getResultJson();
		boolean isLogin = (checkLoginDead(request) != null);// 是否登录
		CommentLike t = new CommentLike();
		if (isLogin) {
			t.setUserId(request.getHeader("userId"));
		}

		Map<String, String> map = null;
		List<Map<String, String>> total = new ArrayList<>();
		for (ArticleCommentVo a : list) {
			map = new HashMap<>();
			map.put("commentId", a.getId());
			map.put("num", a.getNum().toString());
			map.put("replyerName", a.getReplyerName());
			map.put("replyerId", a.getReplyerId());
			map.put("replyerAvatar", a.getReplyerAvatar());
			map.put("parentReplyerId", a.getParentReplyerId());
			map.put("parentReplyerName", a.getParentReplyerName());
			map.put("content", a.getContent());
			map.put("createDate", TimeUtils.format(a.getCreateDate(), "yyyy-MM-dd HH:mm"));
			map.put("isLike", "0");
			if (isLogin) {
				t.setCommentId(a.getId());
				CommentLike cl = this.arCommentDao.getLike(t);
				if (cl != null) {
					map.put("isLike", cl.getStatus() + "");
				}
			}
			total.add(map);
		}
		return mapper.setCount(page.getTotal()).setData(total).getResultJson();
	}

	@Override
	public String newComments(HttpServletRequest request, String articleId) {
		ResponseMapper mapper = ResponseMapper.createMapper();
		List<ArticleCommentVo> list = this.arCommentDao.findNewComments(articleId);
		if (list == null || list.size() < 1) {
			return mapper.getResultJson();
		}
		Map<String, String> map = null;
		List<Map<String, String>> total = new ArrayList<>();
		boolean isLogin = (checkLoginDead(request) != null);// 是否登录
		CommentLike t = new CommentLike();
		if (isLogin) {
			t.setUserId(request.getHeader("userId"));
		}
		for (ArticleCommentVo a : list) {
			map = new HashMap<>();
			map.put("commentId", a.getId());
			map.put("num", a.getNum().toString());
			map.put("replyerName", a.getReplyerName());
			map.put("replyerId", a.getReplyerId());
			map.put("replyerAvatar", a.getReplyerAvatar());
			map.put("parentReplyerId", a.getParentReplyerId());
			map.put("parentReplyerName", a.getParentReplyerName());
			map.put("content", a.getContent());
			map.put("createDate", TimeUtils.format(a.getCreateDate(), "yyyy-MM-dd HH:mm"));
			map.put("isLike", "0");
			if (isLogin) {
				t.setCommentId(a.getId());
				CommentLike cl = this.arCommentDao.getLike(t);
				if (cl != null) {
					map.put("isLike", cl.getStatus() + "");
				}
			}
			total.add(map);
		}

		return mapper.setData(total).getResultJson();
	}

	// =============================评论相关===========================//
	@Override
	public String addCommentLike(HttpServletRequest request, String commentId, Integer isLike) {
		ResponseMapper mapper = ResponseMapper.createMapper();
		if (checkLoginDead(request) == null) {// 没登录 或失效
			if (isLike == 0)
				this.addCommentLikeNum(commentId, true);
			else if (isLike == 1)
				this.addCommentLikeNum(commentId, false);
			return mapper.setCode(ResultConstant.LOGIN_INVALIDATE).getResultJson();
		} else {
			CommentLike t = new CommentLike();
			t.setUserId(request.getHeader("userId")).setCommentId(commentId);
			if (this.arCommentDao.isLiked(t) > 0) {// 已经点过
				CommentLike like = this.arCommentDao.getLikeForUpdate(t);
				if (like.getStatus() == 1) {// 取消点赞
					t.setStatus(0);
					this.addCommentLikeNum(commentId, false);
				} else {// 进行点赞
					t.setStatus(1);
					this.addCommentLikeNum(commentId, true);
				}
				this.arCommentDao.updateLike(t);
			} else {// 没点过赞
				this.arCommentDao.insertLike(t);
				this.addCommentLikeNum(commentId, true);
			}

		}
		return mapper.getResultJson();
	}

	private void addCommentLikeNum(String commentId, boolean flag) {
		ArticleComment ac = this.arCommentDao.getForUpdate(commentId);
		ArticleComment temp = new ArticleComment();
		temp.setId(commentId);
		if (flag) {// 点赞
			temp.setNum(ac.getNum() + 1);
		} else {
			temp.setNum(ac.getNum() - 1);
		}
		this.arCommentDao.update(temp);
	}

	@Override
	public String latestOfUsers(HttpServletRequest request, String[] userIds) {
		ResponseMapper mapper = ResponseMapper.createMapper();
		List<ArticleVo> list = this.articleDao.findLatestOfUsers(userIds);
		if (list != null && list.size() > 0) {
			mapper.setData(list);
		}
		return mapper.getResultJson();
	}

	@Override
	public String search(HttpServletRequest request, String keyword) {
		ResponseMapper mapper = ResponseMapper.createMapper();
		Map<String, Object> map = ElasticUtils.searchWithCount(new String[] { "website" }, new String[] { "article" },
				0, 10, keyword, new String[] { "title", "content" });
		return mapper.setData(map).getResultJson();
	}

	@Override
	public String synElastic(HttpServletRequest request, String password) {
		ResponseMapper mapper = ResponseMapper.createMapper();
		Page<Article> page = new Page<>();
		if ("xiaoyu".equals(password)) {
			int count = this.articleDao.count();
			// 分页同步 防止一次性取出量过大
			for (int i = 1; i <= (count + 50 - 1) / 50; i++) {
				PageHelper.startPage(i, 50, true);
				page = (Page<Article>) this.articleDao.findByList(new Article());
				if (page != null && page.getResult() != null && page.getResult().size() > 0) {
					List<Article> list = page.getResult();
					Map<String,String> jsonMap = new HashMap<>();
					for (Article a : list) {
						jsonMap.put(a.getId(), JSON.toJSONString(a));
						ElasticUtils.upsertList("website", "article", jsonMap);
					}
				}
			}
			return mapper.setMessage("同步成功").getResultJson();
		}

		return mapper.setMessage("同步失败").getResultJson();
	}

}
