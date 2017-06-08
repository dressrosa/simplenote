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

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xiaoyu.common.base.BaseService;
import com.xiaoyu.common.base.ResponseMapper;
import com.xiaoyu.common.base.ResultConstant;
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
import com.xiaoyu.modules.biz.article.service.api.IArticleService;
import com.xiaoyu.modules.biz.article.vo.ArticleCommentVo;
import com.xiaoyu.modules.biz.article.vo.ArticleVo;
import com.xiaoyu.modules.biz.user.dao.UserAttrDao;
import com.xiaoyu.modules.biz.user.dao.UserDao;
import com.xiaoyu.modules.biz.user.entity.User;
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
		map.put("user", this.user2Map(this.userDao.getById(a.getUserId())));
		map.put("attr", a.getAttr());
		return map;
	}

	private Map<String, Object> article2Map1(ArticleVo a) {
		Map<String, Object> map = new HashMap<>();
		map.put("articleId", a.getId());
		map.put("content", a.getContent().length() > 200 ? a.getContent().substring(0, 199) : a.getContent());
		map.put("title", a.getTitle());
		map.put("user", this.user2Map(this.userDao.getById(a.getUserId())));
		map.put("attr", a.getAttr());
		map.put("isLike", "0");
		map.put("isCollect", "0");
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
				this.userAttrDao.addNum(NumCountType.ArticleNum.ordinal(), 1);
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

		for (ArticleVo a : list) {
			Map<String, Object> m = this.article2Map1(a);
			if (checkLoginDead(request) != null) {
				ArticleLike t = new ArticleLike();
				t.setArticleId(a.getId()).setUserId(request.getHeader("userId"));
				ArticleLike al = this.likeDao.get(t);
				if (al != null) {
					m.put("isLike", al.getStatus() + "");
				}
				ArticleCollect t1 = new ArticleCollect();
				t1.setArticleId(a.getId()).setUserId(request.getHeader("userId"));
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
		if (list != null && list.size() > 0) {
			for (ArticleVo a : list) {
				Map<String, Object> m = this.article2Map1(a);
				if (checkLoginDead(request) != null) {
					ArticleLike t = new ArticleLike();
					t.setArticleId(a.getId()).setUserId(request.getHeader("userId"));
					ArticleLike al = this.likeDao.get(t);
					if (al != null) {
						m.put("isLike", al.getStatus() + "");
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

	@Override
	public String addCollect(HttpServletRequest request, String articleId, Integer isCollect) {
		ResponseMapper mapper = ResponseMapper.createMapper();
		if (this.articleDao.isExist(articleId) < 1) {
			return mapper.setCode(ResultConstant.ARGS_ERROR).getResultJson();
		}
		if (checkLoginDead(request) == null) {// 没登录 或失效
			if (isCollect == 0)
				this.addCollectNum(articleId, true);
			else if (isCollect == 1)
				this.addCollectNum(articleId, false);
			return mapper.setCode(ResultConstant.LOGIN_INVALIDATE).getResultJson();
		} else {
			ArticleCollect t = new ArticleCollect();
			t.setUserId(request.getHeader("userId")).setArticleId(articleId);
			if (this.collectDao.isExist(t) > 0) {// 已经收藏
				ArticleCollect co = this.collectDao.getForUpdate(t);
				if (co.getStatus() == 1) {// 取消收藏
					t.setStatus(0);
					this.addCollectNum(articleId, false);

				} else {// 进行收藏
					t.setStatus(1);
					this.addCollectNum(articleId, true);

				}
				this.collectDao.update(t);
			} else {// 没收藏
				t.setId(IdGenerator.uuid());
				this.collectDao.insert(t);
				this.addCollectNum(articleId, true);

			}

		}
		return mapper.getResultJson();

	}

	private void addCollectNum(String articleId, boolean flag) {
		ArticleAttr attr = new ArticleAttr();
		ArticleAttr at = this.attrDao.getForUpdate(articleId);
		attr.setArticleId(articleId);
		if (flag) {// 收藏
			attr.setCollectNum(at.getCollectNum() + 1);
			try {
				this.userAttrDao.addNum(NumCountType.CollectNum.ordinal(), 1);
			} catch (Exception e) {
				// do nothing
			}
		} else {
			attr.setCollectNum(at.getCollectNum() - 1);
			try {
				this.userAttrDao.addNum(NumCountType.CollectNum.ordinal(), -1);
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
		this.arCommentDao.insert(co);
		Map<String, String> map = new HashMap<>();
		map.put("replyerId", user.getId());
		map.put("replyerName", user.getNickname());
		map.put("replyerAvatar", user.getAvatar());
		map.put("content", content);
		map.put("createDate", TimeUtils.format(new Date(), "yyyy-MM-dd HH:mm"));
		return mapper.setData(map).getResultJson();
	}

	@Override
	public String comments(HttpServletRequest request, String articleId, Integer pageNum) {
		ResponseMapper mapper = ResponseMapper.createMapper();
		PageHelper.startPage(pageNum, 10);
		Page<ArticleCommentVo> page = (Page<ArticleCommentVo>) this.arCommentDao.findList(articleId);
		List<ArticleCommentVo> list = page.getResult();
		if (list == null || list.size() < 1)
			return mapper.getResultJson();
		Map<String, String> map = null;
		List<Map<String, String>> total = new ArrayList<>();
		for (ArticleCommentVo a : list) {
			map = new HashMap<>();
			map.put("commentId", a.getId());
			map.put("replyerName", a.getReplyerName());
			map.put("replyerId", a.getReplyerId());
			map.put("replyerAvatar", a.getReplyerAvatar());
			map.put("parentReplyerId", a.getParentReplyerId());
			map.put("parentReplyerName", a.getParentReplyerName());
			map.put("content", a.getContent());
			map.put("createDate", TimeUtils.format(a.getCreateDate(), "yyyy-MM-dd HH:mm"));
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
		for (ArticleCommentVo a : list) {
			map = new HashMap<>();
			map.put("commentId", a.getId());
			map.put("replyerName", a.getReplyerName());
			map.put("replyerId", a.getReplyerId());
			map.put("replyerAvatar", a.getReplyerAvatar());
			map.put("parentReplyerId", a.getParentReplyerId());
			map.put("parentReplyerName", a.getParentReplyerName());
			map.put("content", a.getContent());
			map.put("createDate", TimeUtils.format(a.getCreateDate(), "yyyy-MM-dd HH:mm"));
			total.add(map);
		}

		return mapper.setData(total).getResultJson();
	}
}
