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
import com.xiaoyu.common.base.ResponseCode;
import com.xiaoyu.common.utils.ElasticUtils;
import com.xiaoyu.common.utils.IdGenerator;
import com.xiaoyu.common.utils.JedisUtils;
import com.xiaoyu.common.utils.TimeUtils;
import com.xiaoyu.common.utils.UserUtils;
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
import com.xiaoyu.modules.biz.message.entity.Message;
import com.xiaoyu.modules.biz.message.service.MessageHandler;
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
    @Autowired
    private MessageHandler msgHandler;

    private Map<String, Object> article2Map(ArticleVo a) {
        final Map<String, Object> map = new HashMap<>();
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
        final Map<String, Object> map = new HashMap<>();
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
        final Map<String, Object> map = new HashMap<>();
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

    // 发送消息
    private void sendMsg(String userId, int type, String bizId, int bizType, int bizAction, String content,
            String reply) {
        final Message msg = new Message();
        msg.setSenderId(userId).setType(type).setBizId(bizId).setBizType(bizType).setBizAction(bizAction);
        if (content != null) {
            msg.setContent(content);
        }
        if (reply != null) {
            msg.setReply(reply);
        }
        try {
            this.msgHandler.produce(JSON.toJSONString(msg));
        } catch (final Exception e) {
            e.printStackTrace();
            // do nothing
        }

    }

    @Override
    public String detail(String articleId) {
        final ResponseMapper mapper = ResponseMapper.createMapper();
        final ArticleVo a = this.articleDao.getVo(articleId);
        if (a == null) {
            return mapper.code(ResponseCode.NO_DATA.statusCode()).resultJson();
        }
        return mapper.data(this.article2Map(a)).resultJson();
    }

    @Transactional(readOnly = false)
    private String publish(String userId, String title, String content) {
        final ResponseMapper mapper = ResponseMapper.createMapper();
        final Article t = new Article();
        final ArticleAttr attr = new ArticleAttr();
        t.setId(IdGenerator.uuid());
        t.setContent(content).setTitle(title).setUserId(userId);
        try {
            this.articleDao.insert(t);
            attr.setArticleId(t.getId());
            attr.setId(IdGenerator.uuid());
            this.attrDao.insert(attr);
            try {// 增加发文数
                this.userAttrDao.addNum(NumCountType.ArticleNum.ordinal(), 1, userId);
            } catch (final Exception e) {
                // do nothing
            }
        } catch (final RuntimeException e) {
            throw e;
        }
        return mapper.data(t.getId()).resultJson();
    }

    @Override
    public String hotList(HttpServletRequest request) {
        final ResponseMapper mapper = ResponseMapper.createMapper();
        System.out.println(request.getHeader("userId"));
        // if (EhCacheUtil.IsExist("SystemCache")) {// 从缓存取
        // @SuppressWarnings("unchecked")
        // List<Object> total = (List<Object>) EhCacheUtil.get("SystemCache",
        // "pageList");
        // if (total != null && total.size() > 0) {
        // return mapper.data(total).resultJson();
        // }
        // }
        PageHelper.startPage(1, 12);
        final Page<ArticleVo> page = (Page<ArticleVo>) this.articleDao.findHotList();
        final List<ArticleVo> list = page.getResult();
        final List<Object> total = new ArrayList<>();

        final boolean isLogin = (UserUtils.checkLoginDead(request) != null);// 是否登录
        final ArticleLike t = new ArticleLike();
        t.setUserId(request.getHeader("userId"));
        final ArticleCollect t1 = new ArticleCollect();
        t1.setUserId(request.getHeader("userId"));

        for (final ArticleVo a : list) {
            final Map<String, Object> m = this.article2Map1(a);
            if (isLogin) {
                t.setArticleId(a.getId());
                final ArticleLike al = this.likeDao.get(t);
                if (al != null) {
                    m.put("isLike", al.getStatus() + "");
                }
                t1.setArticleId(a.getId());
                final ArticleCollect ac = this.collectDao.get(t1);
                if (ac != null) {
                    m.put("isCollect", ac.getStatus() + "");
                }

            }
            total.add(m);
        }
        // EhCacheUtil.put("SystemCache", "pageList", total);// 存入缓存
        return mapper.data(total).resultJson();
    }

    @Override
    public String list(HttpServletRequest request, String userId, Integer pageNum, Integer pageSize) {
        final ResponseMapper mapper = ResponseMapper.createMapper();
        final Article article = new Article();
        article.setUserId(userId);
        if (pageNum == null || pageSize == null || pageNum < 0 || pageSize < 0) {
            pageNum = 0;
            pageSize = 10;
        }

        final Page<ArticleVo> page = this.findByPageWithAttr(userId, pageNum, pageSize);
        final List<ArticleVo> list = page.getResult();
        final List<Map<String, Object>> total = new ArrayList<>();

        final boolean isLogin = (UserUtils.checkLoginDead(request) != null);// 是否登录
        final ArticleLike t = new ArticleLike();
        t.setUserId(request.getHeader("userId"));
        final ArticleCollect t1 = new ArticleCollect();
        t1.setUserId(request.getHeader("userId"));

        if (list != null && list.size() > 0) {
            for (final ArticleVo a : list) {
                final Map<String, Object> m = this.article2Map1(a);
                if (isLogin) {
                    t.setArticleId(a.getId());
                    final ArticleLike al = this.likeDao.get(t);
                    if (al != null) {
                        m.put("isLike", al.getStatus() + "");
                    }
                    t1.setArticleId(a.getId());
                    final ArticleCollect ac = this.collectDao.get(t1);
                    if (ac != null) {
                        m.put("isCollect", ac.getStatus() + "");
                    }

                }
                total.add(m);
            }
        }
        return mapper.data(total).resultJson();
    }

    @Override
    public String collectList(HttpServletRequest request, String userId, Integer pageNum, Integer pageSize) {
        final ResponseMapper mapper = ResponseMapper.createMapper();
        final boolean isLogin = (UserUtils.checkLoginDead(request) != null);// 是否登录
        // if (!isLogin) {
        // return
        // mapper.code(ResponseCode.LOGIN_INVALIDATE).message(ResponseCode.LOGIN_INVALIDATE_MESSAGE)
        // .resultJson();
        // }
        // if (!userId.equals(request.getHeader("userId"))) {
        // return
        // mapper.code(ResponseCode.REQ_NOACCESS).message(ResponseCode.REQ_NOACCESS_MESSAGE)
        // .resultJson();
        // }
        final Article article = new Article();
        article.setUserId(userId);
        if (pageNum == null || pageSize == null || pageNum < 0 || pageSize < 0) {
            pageNum = 0;
            pageSize = 10;
        }

        Page<ArticleVo> page = new Page<ArticleVo>();
        PageHelper.startPage(pageNum, pageSize);
        page = this.articleDao.findCollectList(userId);
        final List<ArticleVo> list = page.getResult();
        final List<Map<String, Object>> total = new ArrayList<>();

        final ArticleLike t = new ArticleLike();
        t.setUserId(request.getHeader("userId"));
        final ArticleCollect t1 = new ArticleCollect();
        t1.setUserId(request.getHeader("userId"));

        if (list != null && list.size() > 0) {
            for (final ArticleVo a : list) {
                final Map<String, Object> m = this.article2Map1(a);
                final Map<String, Object> map = new HashMap<>();
                map.put("articleId", a.getId());
                map.put("title", a.getTitle());
                map.put("user", this.user2Map(this.userDao.getVoById(a.getUserId())));
                map.put("attr", a.getAttr());
                map.put("isLike", "0");
                map.put("isCollect", "0");

                if (isLogin) {
                    t.setArticleId(a.getId());
                    final ArticleLike al = this.likeDao.get(t);
                    if (al != null) {
                        m.put("isLike", al.getStatus() + "");
                    }
                    t1.setArticleId(a.getId());
                    final ArticleCollect ac = this.collectDao.get(t1);
                    if (ac != null) {
                        m.put("isCollect", ac.getStatus() + "");
                    }

                }
                total.add(m);
            }
        }
        return mapper.data(total).resultJson();
    }

    private Page<ArticleVo> findByPageWithAttr(String userId, int pageNum, int pageSize) {
        Page<ArticleVo> page = new Page<ArticleVo>();
        PageHelper.startPage(pageNum, pageSize);
        page = (Page<ArticleVo>) this.articleDao.findByListWithAttr(userId);
        return page;
    }

    @Override
    public String addArticle(HttpServletRequest request, String title, String content, String userId, String token) {
        final ResponseMapper mapper = ResponseMapper.createMapper();
        final HttpSession session = request.getSession(false);
        if (session == null) {
            return mapper.code(ResponseCode.LOGIN_INVALIDATE.statusCode()).message("登录失效,请刷新登录1").resultJson();
        }
        final User user = (User) session.getAttribute(token);
        if (user == null) {
            return mapper.code(ResponseCode.LOGIN_INVALIDATE.statusCode()).message("登录失效,请刷新登录2").resultJson();
        }
        if (!userId.equals(user.getId())) {
            return mapper.code(ResponseCode.LOGIN_INVALIDATE.statusCode()).message("登录失效,请刷新登录3").resultJson();
        }
        return this.publish(userId, title, content);
    }

    @Override
    public String addReadNum(HttpServletRequest request, String articleId) {
        final String ip = request.getRemoteHost();
        if (JedisUtils.get("user:login:" + ip) != null) {
            return ResponseMapper.createMapper().resultJson();
        }
        final ArticleAttr attr = this.attrDao.getForUpdate(articleId);// 行级锁
        final ArticleAttr temp = new ArticleAttr();
        temp.setId(attr.getId());
        temp.setArticleId(attr.getArticleId());
        temp.setReadNum(attr.getReadNum() + 1);
        this.attrDao.update(temp);
        JedisUtils.set("user:login:" + ip, temp.getReadNum().toString(), 60 * 10);
        return ResponseMapper.createMapper().data(temp.getReadNum()).resultJson();
    }

    @Autowired
    private ArticleLikeDao likeDao;

    @Override
    public String addLike(HttpServletRequest request, String articleId, Integer isLike) {
        final ResponseMapper mapper = ResponseMapper.createMapper();
        if (this.articleDao.isExist(articleId) < 1) {
            return mapper.code(ResponseCode.ARGS_ERROR.statusCode()).resultJson();
        }
        if (UserUtils.checkLoginDead(request) == null) {// 没登录 或失效
            if (isLike == 0) {
                this.addLikeNum(articleId, true);
            } else if (isLike == 1) {
                this.addLikeNum(articleId, false);
            }
            return mapper.code(ResponseCode.LOGIN_INVALIDATE.statusCode()).resultJson();
        } else {
            final ArticleLike t = new ArticleLike();
            t.setUserId(request.getHeader("userId")).setArticleId(articleId);

            if (this.likeDao.isExist(t) > 0) {// 已经点过
                final ArticleLike like = this.likeDao.getForUpdate(t);
                if (like.getStatus() == 1) {// 取消点赞
                    t.setStatus(0);
                    this.addLikeNum(articleId, false);
                } else {// 进行点赞
                    t.setNum(like.getNum() + 1).setStatus(1);
                    this.addLikeNum(articleId, true);
                }
                if (this.likeDao.update(t) > 0) {
                    if (like.getStatus() == 0) {// 点赞的
                        // 消息推送
                        this.sendMsg(request.getHeader("userId"), 0, articleId, 0, 2, null, null);
                    }
                }
            } else {// 没点过赞
                t.setNum(1);
                if (this.likeDao.insert(t) > 0) {
                    // 消息推送
                    this.sendMsg(request.getHeader("userId"), 0, articleId, 0, 2, null, null);
                }
                this.addLikeNum(articleId, true);
            }

        }
        return mapper.resultJson();

    }

    private void addLikeNum(String articleId, boolean flag) {
        final ArticleAttr attr = new ArticleAttr();
        final ArticleAttr at = this.attrDao.getForUpdate(articleId);
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
        final ResponseMapper mapper = ResponseMapper.createMapper();
        if (this.articleDao.isExist(articleId) < 1) {
            return mapper.code(ResponseCode.ARGS_ERROR.statusCode()).resultJson();
        }
        final String userId = request.getHeader("userId");
        if (UserUtils.checkLoginDead(request) == null) {// 没登录 或失效
            if (isCollect == 0) {
                this.addCollectNum(articleId, userId, true);
            } else if (isCollect == 1) {
                this.addCollectNum(articleId, userId, false);
            }
            return mapper.code(ResponseCode.LOGIN_INVALIDATE.statusCode()).resultJson();
        } else {
            final ArticleCollect t = new ArticleCollect();
            t.setUserId(userId).setArticleId(articleId);
            if (this.collectDao.isExist(t) > 0) {// 已经收藏
                final ArticleCollect co = this.collectDao.getForUpdate(t);
                if (co.getStatus() == 1) {// 取消收藏
                    t.setStatus(0);
                    this.addCollectNum(articleId, userId, false);
                } else {// 进行收藏
                    t.setStatus(1);
                    if (this.addCollectNum(articleId, userId, true) > 0) {
                        // 消息推送
                        this.sendMsg(userId, 0, articleId, 0, 3, null, null);
                    }
                }
                this.collectDao.update(t);
            } else {// 没收藏
                t.setId(IdGenerator.uuid());
                this.collectDao.insert(t);
                if (this.addCollectNum(articleId, userId, true) > 0) {
                    // 消息推送
                    this.sendMsg(userId, 0, articleId, 0, 3, null, null);
                }
            }

        }
        return mapper.resultJson();

    }

    private int addCollectNum(String articleId, String userId, boolean flag) {
        final ArticleAttr attr = new ArticleAttr();
        final ArticleAttr at = this.attrDao.getForUpdate(articleId);
        attr.setArticleId(articleId);
        if (flag) {// 收藏
            attr.setCollectNum(at.getCollectNum() + 1);
            try {
                this.userAttrDao.addNum(NumCountType.CollectNum.ordinal(), 1, userId);
            } catch (final Exception e) {
                // do nothing
            }
        } else {
            attr.setCollectNum(at.getCollectNum() - 1);
            try {
                this.userAttrDao.addNum(NumCountType.CollectNum.ordinal(), -1, userId);
            } catch (final Exception e) {
                // do nothing
            }
        }
        return this.attrDao.update(attr);
    }

    @Autowired
    private ArticleCommentDao arCommentDao;

    @Override
    public String comment(HttpServletRequest request, String articleId, String content) {
        final ResponseMapper mapper = ResponseMapper.createMapper();
        if (org.apache.commons.lang3.StringUtils.isBlank(content)) {
            return mapper.code(ResponseCode.ARGS_ERROR.statusCode()).resultJson();
        }
        final User user = UserUtils.checkLoginDead(request);
        if (user == null) {
            return mapper.code(ResponseCode.LOGIN_INVALIDATE.statusCode()).resultJson();
        }
        final ArticleComment co = new ArticleComment();
        final Article ar = this.articleDao.getById(articleId);
        if (ar == null) {
            return mapper.code(ResponseCode.ARGS_ERROR.statusCode()).resultJson();
        }
        co.setArticleId(articleId).setReplyerId(user.getId()).setContent(content).setAuthorId(ar.getUserId())
                .setId(IdGenerator.uuid());
        if (this.arCommentDao.insert(co) > 0) {
            this.addCommentNum(articleId, true);
            // 消息推送
            this.sendMsg(user.getId(), 0, articleId, 0, 1, content, null);
        }
        final Map<String, String> map = new HashMap<>();
        map.put("replyerId", user.getId());
        map.put("replyerName", user.getNickname());
        map.put("replyerAvatar", user.getAvatar());
        map.put("content", content);
        map.put("createDate", TimeUtils.format(new Date(), "yyyy-MM-dd HH:mm"));
        return mapper.data(map).resultJson();
    }

    private void addCommentNum(String articleId, boolean flag) {
        final ArticleAttr attr = new ArticleAttr();
        final ArticleAttr at = this.attrDao.getForUpdate(articleId);
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
        final ResponseMapper mapper = ResponseMapper.createMapper();
        PageHelper.startPage(pageNum, 10);
        final Page<ArticleCommentVo> page = (Page<ArticleCommentVo>) this.arCommentDao.findList(articleId);
        final List<ArticleCommentVo> list = page.getResult();
        if (list == null || list.size() < 1) {
            return mapper.resultJson();
        }
        final boolean isLogin = (UserUtils.checkLoginDead(request) != null);// 是否登录
        final CommentLike t = new CommentLike();
        if (isLogin) {
            t.setUserId(request.getHeader("userId"));
        }

        Map<String, String> map = null;
        final List<Map<String, String>> total = new ArrayList<>();
        for (final ArticleCommentVo a : list) {
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
                final CommentLike cl = this.arCommentDao.getLike(t);
                if (cl != null) {
                    map.put("isLike", cl.getStatus() + "");
                }
            }
            total.add(map);
        }
        return mapper.count(page.getTotal()).data(total).resultJson();
    }

    @Override
    public String newComments(HttpServletRequest request, String articleId) {
        final ResponseMapper mapper = ResponseMapper.createMapper();
        final List<ArticleCommentVo> list = this.arCommentDao.findNewComments(articleId);
        if (list == null || list.size() < 1) {
            return mapper.resultJson();
        }
        Map<String, String> map = null;
        final List<Map<String, String>> total = new ArrayList<>();
        final boolean isLogin = (UserUtils.checkLoginDead(request) != null);// 是否登录
        final CommentLike t = new CommentLike();
        if (isLogin) {
            t.setUserId(request.getHeader("userId"));
        }
        for (final ArticleCommentVo a : list) {
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
                final CommentLike cl = this.arCommentDao.getLike(t);
                if (cl != null) {
                    map.put("isLike", cl.getStatus() + "");
                }
            }
            total.add(map);
        }

        return mapper.data(total).resultJson();
    }

    // =============================评论相关===========================//
    @Override
    public String addCommentLike(HttpServletRequest request, String commentId, Integer isLike) {
        final ResponseMapper mapper = ResponseMapper.createMapper();
        if (UserUtils.checkLoginDead(request) == null) {// 没登录 或失效
            if (isLike == 0) {
                this.addCommentLikeNum(commentId, true);
            } else if (isLike == 1) {
                this.addCommentLikeNum(commentId, false);
            }
            return mapper.code(ResponseCode.LOGIN_INVALIDATE.statusCode()).resultJson();
        } else {
            final CommentLike t = new CommentLike();
            t.setUserId(request.getHeader("userId")).setCommentId(commentId);
            if (this.arCommentDao.isLiked(t) > 0) {// 已经点过
                final CommentLike like = this.arCommentDao.getLikeForUpdate(t);
                if (like.getStatus() == 1) {// 取消点赞
                    t.setStatus(0);
                    this.addCommentLikeNum(commentId, false);
                } else {// 进行点赞
                    t.setStatus(1);
                    if (this.addCommentLikeNum(commentId, true) > 0) {
                        // 消息推送
                        this.sendMsg(request.getHeader("userId"), 0, commentId, 0, 6, null, null);
                    }
                }
                this.arCommentDao.updateLike(t);
            } else {// 没点过赞
                if (this.arCommentDao.insertLike(t) > 0) {
                    // 消息推送
                    this.sendMsg(request.getHeader("userId"), 0, commentId, 0, 6, null, null);
                }
                this.addCommentLikeNum(commentId, true);
            }

        }
        return mapper.resultJson();
    }

    private int addCommentLikeNum(String commentId, boolean flag) {
        final ArticleComment ac = this.arCommentDao.getForUpdate(commentId);
        final ArticleComment temp = new ArticleComment();
        temp.setId(commentId);
        if (flag) {// 点赞
            temp.setNum(ac.getNum() + 1);
        } else {
            temp.setNum(ac.getNum() - 1);
        }
        return this.arCommentDao.update(temp);
    }

    @Override
    public String latestOfUsers(HttpServletRequest request, String[] userIds) {
        final ResponseMapper mapper = ResponseMapper.createMapper();
        final List<ArticleVo> list = this.articleDao.findLatestOfUsers(userIds);
        if (list != null && list.size() > 0) {
            mapper.data(list);
        }
        return mapper.resultJson();
    }

    @Override
    public String search(HttpServletRequest request, String keyword) {
        final ResponseMapper mapper = ResponseMapper.createMapper();
        final Map<String, Object> map = ElasticUtils.searchWithCount(new String[] { "website" },
                new String[] { "article" }, 0, 10, keyword, new String[] { "title", "content" });
        return mapper.data(map).resultJson();
    }

    @Override
    public String synElastic(HttpServletRequest request, String password) {
        final ResponseMapper mapper = ResponseMapper.createMapper();
        Page<Article> page = new Page<>();
        if ("xiaoyu".equals(password)) {
            final int count = this.articleDao.count();
            // 分页同步 防止一次性取出量过大
            for (int i = 1; i <= (count + 50 - 1) / 50; i++) {
                PageHelper.startPage(i, 50, true);
                page = (Page<Article>) this.articleDao.findByList(new Article());
                if (page != null && page.getResult() != null && page.getResult().size() > 0) {
                    final List<Article> list = page.getResult();
                    final Map<String, String> jsonMap = new HashMap<>();
                    for (final Article a : list) {
                        jsonMap.put(a.getId(), JSON.toJSONString(a));
                        ElasticUtils.upsertList("website", "article", jsonMap);
                    }
                }
            }
            return mapper.message("同步成功").resultJson();
        }

        return mapper.message("同步失败").resultJson();
    }

}
