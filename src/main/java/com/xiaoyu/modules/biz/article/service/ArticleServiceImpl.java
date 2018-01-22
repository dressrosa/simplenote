/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.article.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xiaoyu.common.base.BaseService;
import com.xiaoyu.common.base.ResponseCode;
import com.xiaoyu.common.base.ResponseMapper;
import com.xiaoyu.common.utils.ElasticUtils;
import com.xiaoyu.common.utils.IdGenerator;
import com.xiaoyu.common.utils.JedisUtils;
import com.xiaoyu.common.utils.StringUtil;
import com.xiaoyu.common.utils.TimeUtils;
import com.xiaoyu.common.utils.UserUtils;
import com.xiaoyu.maple.core.MapleUtil;
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
import com.xiaoyu.modules.biz.message.service.api.IMessageService;
import com.xiaoyu.modules.biz.user.dao.UserAttrDao;
import com.xiaoyu.modules.biz.user.dao.UserDao;
import com.xiaoyu.modules.biz.user.entity.User;
import com.xiaoyu.modules.biz.user.vo.UserVo;
import com.xiaoyu.modules.constant.BizAction;
import com.xiaoyu.modules.constant.BizType;
import com.xiaoyu.modules.constant.MsgType;
import com.xiaoyu.modules.constant.NumCountType;

/**
 * @author xiaoyu 2016年3月29日
 */
@Service
@Primary
public class ArticleServiceImpl extends BaseService<ArticleDao, Article> implements IArticleService {

    private static final Logger LOG = LoggerFactory.getLogger(ArticleServiceImpl.class);

    @Autowired
    private UserDao userDao;
    @Autowired
    private ArticleDao articleDao;
    @Autowired
    private ArticleAttrDao articleAttrDao;
    @Autowired
    private ArticleCollectDao collectDao;
    @Autowired
    private UserAttrDao userAttrDao;
    @Autowired
    private ArticleCommentDao arCommentDao;
    @Autowired
    private ArticleLikeDao likeDao;

    @Autowired
    private IMessageService messageService;

    private Map<String, Object> article2Map(ArticleVo a) {
        return MapleUtil.wrap(a)
                .rename("uuid", "articleId")
                .stick("content", a.getContent().length() > 150 ? a.getContent().substring(0, 149) : a.getContent())
                .stick("createDate", TimeUtils.format(a.getCreateDate(), "yyyy-MM-dd HH:mm"))
                .stick("isLike", "0")
                .stick("isCollect", "0")
                .map();
    }

    @Override
    public String detail(String articleId) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        ArticleVo a = this.articleDao.getVoByUuid(articleId);
        if (a == null) {
            return mapper.code(ResponseCode.NO_DATA.statusCode()).resultJson();
        }
        Map<String, Object> result = MapleUtil.wrap(a)
                .rename("uuid", "articleId")
                .stick("createDate", TimeUtils.format(a.getCreateDate(), "yyyy-MM-dd"))
                .stick("createTime", TimeUtils.format(a.getCreateDate(), "HH:mm"))
                .stick("user", this.userDao.getVoByUuid(a.getUserId()))
                .map();
        return mapper.data(result).resultJson();
    }

    @Override
    public String hotList(HttpServletRequest request) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        // if (EhCacheUtil.IsExist("SystemCache")) {// 从缓存取
        // @SuppressWarnings("unchecked")
        // List<Object> total = (List<Object>) EhCacheUtil.get("SystemCache",
        // "article:hotList");
        // if (total != null && total.size() > 0) {
        // return mapper.data(total).resultJson();
        // }
        // }
        PageHelper.startPage(1, 12);
        Page<ArticleVo> page = (Page<ArticleVo>) this.articleDao.findHotList();
        List<ArticleVo> list = page.getResult();
        // 是否登录
        boolean isLogin = (UserUtils.checkLoginDead(request) != null);
        String tuserId = request.getHeader("userId");
        List<Map<String, Object>> total = this.handleArticleVoList(list, tuserId, isLogin);
        // EhCacheUtil.put("SystemCache", "article:hotList", total);// 存入缓存
        return mapper.data(total).resultJson();
    }

    @Override
    public String list(HttpServletRequest request, String userId, Integer pageNum, Integer pageSize) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        final Article article = new Article();
        article.setUserId(userId);
        if (pageNum == null || pageSize == null || pageNum < 0 || pageSize < 0) {
            pageNum = 0;
            pageSize = 10;
        }

        Page<ArticleVo> page = this.findByPageWithAttr(userId, pageNum, pageSize);
        final List<ArticleVo> list = page.getResult();
        if (list == null || list.isEmpty()) {
            return mapper.resultJson();
        }
        // 是否登录
        boolean isLogin = (UserUtils.checkLoginDead(request) != null);
        String tuserId = request.getHeader("userId");
        List<Map<String, Object>> total = this.handleArticleVoList(list, tuserId, isLogin);

        return mapper.data(total).resultJson();
    }

    @Override
    public String collectList(HttpServletRequest request, String userId, Integer pageNum, Integer pageSize) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        // 是否登录
        final boolean isLogin = (UserUtils.checkLoginDead(request) != null);
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
        String tuserId = request.getHeader("userId");

        List<Map<String, Object>> total = this.handleArticleVoList(list, tuserId, isLogin);
        return mapper.data(total).resultJson();
    }

    private List<Map<String, Object>> handleArticleVoList(final List<ArticleVo> list, final String tuserId,
            final boolean isLogin) {
        final List<Map<String, Object>> total = new ArrayList<>(list.size());
        if (list.isEmpty()) {
            return total;
        }
        ArticleLike t = new ArticleLike();
        ArticleCollect t1 = new ArticleCollect();

        List<String> userIdList = new ArrayList<>(list.size());
        List<ArticleLike> likeQueryList = new ArrayList<>(list.size());
        List<ArticleCollect> collectQueryList = new ArrayList<>(list.size());

        // 设置查询条件
        for (ArticleVo a : list) {
            t = new ArticleLike();
            t.setUserId(tuserId).setArticleId(a.getUuid());
            t1 = new ArticleCollect();
            t1.setUserId(tuserId).setArticleId(a.getUuid());
            userIdList.add(a.getUserId());
            likeQueryList.add(t);
            collectQueryList.add(t1);
        }
        // 批量查询
        List<UserVo> userVoList = this.userDao.findVoByUuid(userIdList);
        List<ArticleLike> likeList = null;
        List<ArticleCollect> collectList = null;
        if (isLogin) {
            likeList = this.likeDao.findListByBatch(likeQueryList);
            collectList = this.collectDao.findListByBatch(collectQueryList);
        }

        Map<String, Object> map = null;
        for (ArticleVo a : list) {
            String tuserId1 = a.getUserId();
            String tarticleId1 = a.getUuid();
            for (UserVo u : userVoList) {
                if (u.getUserId().equals(tuserId1)) {
                    a.setUser(u);
                    break;
                }
            }
            map = this.article2Map(a);
            if (!isLogin) {
                total.add(map);
                continue;
            }

            if (likeList != null && !likeList.isEmpty()) {
                for (ArticleLike li : likeList) {
                    if (tarticleId1.equals(li.getArticleId()) && tuserId1.equals(li.getUserId())) {
                        map.put("isLike", li.getStatus() + "");
                        break;
                    }
                }
            }
            if (collectList != null && !collectList.isEmpty()) {
                for (ArticleCollect c : collectList) {
                    if (tarticleId1.equals(c.getArticleId()) && tuserId1.equals(c.getUserId())) {
                        map.put("isCollect", c.getStatus() + "");
                        break;
                    }
                }
            }
            total.add(map);
        }
        return total;
    }

    private Page<ArticleVo> findByPageWithAttr(String userId, int pageNum, int pageSize) {
        Page<ArticleVo> page = new Page<ArticleVo>();
        PageHelper.startPage(pageNum, pageSize);
        page = (Page<ArticleVo>) this.articleDao.findByListWithAttr(userId);
        return page;
    }

    @Override
    public String addArticle(HttpServletRequest request, String title, String content, String userId) {
        if (UserUtils.checkLoginDead(request) == null) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.LOGIN_INVALIDATE.statusCode())
                    .message("登录失效,请重新登录")
                    .resultJson();
        }
        return this.doPublish(userId, title, content);
    }

    @Transactional(readOnly = false, rollbackFor = RuntimeException.class)
    private String doPublish(String userId, String title, String content) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        Article t = new Article();
        ArticleAttr attr = new ArticleAttr();
        t.setContent(content)
                .setTitle(title)
                .setUserId(userId)
                .setUuid(IdGenerator.uuid());
        try {
            attr.setArticleId(t.getUuid())
                    .setUuid(IdGenerator.uuid());
            this.articleDao.insert(t);
            this.articleAttrDao.insert(attr);
            // 增加发文数
            this.userAttrDao.addNum(NumCountType.ArticleNum.ordinal(), 1, userId);
        } catch (RuntimeException e) {
            LOG.error("publish artile failed,then rollback", e);
            throw e;
        }
        return mapper.data(t.getUuid()).resultJson();
    }

    @Override
    public String editArticle(HttpServletRequest request, String title, String content, String userId, String articleId) {
        if (UserUtils.checkLoginDead(request) == null) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.LOGIN_INVALIDATE.statusCode())
                    .message("登录失效,请重新登录")
                    .resultJson();
        }
        return this.doEdit(userId, title, content, articleId);
    }

    private String doEdit(String userId, String title, String content, String articleId) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        Article ar = this.get(articleId);
        if (ar == null || !userId.equals(ar.getUserId())) {
            return mapper.code(ResponseCode.NO_DATA.statusCode()).resultJson();
        }
        Article temp = new Article();
        temp.setTitle(title)
                .setContent(content)
                .setUuid(articleId);
        if (this.articleDao.update(temp) <= 0) {
            return mapper.code(ResponseCode.FAILED.statusCode()).resultJson();
        }
        return mapper.data(articleId).resultJson();
    }

    @Override
    public String addReadNum(HttpServletRequest request, String articleId) {
        final String ip = request.getRemoteHost();
        if (JedisUtils.get("user:login:" + ip) != null) {
            return ResponseMapper.createMapper().resultJson();
        }
        ArticleAttr temp = new ArticleAttr();
        // 行级锁
        final ArticleAttr attr = this.articleAttrDao.getForUpdate(articleId);
        temp.setArticleId(attr.getArticleId())
                .setReadNum(attr.getReadNum() + 1)
                .setId(attr.getId());
        this.articleAttrDao.update(temp);
        JedisUtils.set("user:login:" + ip, temp.getReadNum().toString(), 60 * 10);
        return ResponseMapper.createMapper().data(temp.getReadNum()).resultJson();
    }

    @Override
    public String addLike(HttpServletRequest request, String articleId, Integer isLike) {
        final ResponseMapper mapper = ResponseMapper.createMapper();
        if (this.articleDao.isExist(articleId) < 1) {
            return mapper.code(ResponseCode.ARGS_ERROR.statusCode()).resultJson();
        }
        ArticleLike t = new ArticleLike();
        t.setUserId(request.getHeader("userId"))
                .setArticleId(articleId);
        // 没登录 或失效
        if (UserUtils.checkLoginDead(request) == null) {
            if (isLike == 0) {
                this.addLikeNum(t, true);
            } else if (isLike == 1) {
                this.addLikeNum(t, false);
            }
            return mapper.code(ResponseCode.LOGIN_INVALIDATE.statusCode()).resultJson();
        }

        this.handleLikeOrCollectNum(t);
        return mapper.resultJson();

    }

    private void addLikeNum(ArticleLike a, boolean flag) {
        ArticleAttr attr = new ArticleAttr();
        attr.setArticleId(a.getArticleId());
        final ArticleAttr at = this.articleAttrDao.getForUpdate(a.getArticleId());
        if (flag) {// 点赞
            attr.setLikeNum(at.getLikeNum() + 1);
        } else {
            attr.setLikeNum(at.getLikeNum() - 1);
        }
        this.articleAttrDao.update(attr);
    }

    /*
     * isCollect 0取消收藏 1收藏
     */
    @Override
    public String addCollect(HttpServletRequest request, String articleId, Integer isCollect) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        if (this.articleDao.isExist(articleId) < 1) {
            return mapper.code(ResponseCode.ARGS_ERROR.statusCode()).resultJson();
        }
        String userId = request.getHeader("userId");
        ArticleCollect t = new ArticleCollect();
        t.setUserId(userId).setArticleId(articleId);
        // 没登录 或失效
        if (UserUtils.checkLoginDead(request) == null) {
            if (isCollect == 0) {
                this.addCollectNum(t, true);
            } else if (isCollect == 1) {
                this.addCollectNum(t, false);
            }
            return mapper.code(ResponseCode.LOGIN_INVALIDATE.statusCode()).resultJson();
        }
        this.handleLikeOrCollectNum(t);
        return mapper.resultJson();

    }

    private int addCollectNum(ArticleCollect a, boolean flag) {
        ArticleAttr attr = new ArticleAttr();
        attr.setArticleId(a.getArticleId());
        final ArticleAttr at = this.articleAttrDao.getForUpdate(a.getArticleId());
        // 收藏
        if (flag) {
            attr.setCollectNum(at.getCollectNum() + 1);
            this.userAttrDao.addNum(NumCountType.CollectNum.ordinal(), 1, a.getUserId());
        } else {
            attr.setCollectNum(at.getCollectNum() - 1);
            this.userAttrDao.addNum(NumCountType.CollectNum.ordinal(), -1, a.getUserId());
        }
        return this.articleAttrDao.update(attr);
    }

    @Override
    public String comment(HttpServletRequest request, String articleId, String content) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        if (StringUtil.isBlank(content)) {
            return mapper.code(ResponseCode.ARGS_ERROR.statusCode()).resultJson();
        }
        final User user = UserUtils.checkLoginDead(request);
        if (user == null) {
            return mapper.code(ResponseCode.LOGIN_INVALIDATE.statusCode()).resultJson();
        }
        boolean isSendMsg = false;
        ArticleComment co = new ArticleComment();
        Article ar = this.articleDao.getByUuid(articleId);
        if (ar == null) {
            return mapper.code(ResponseCode.ARGS_ERROR.statusCode()).resultJson();
        }
        co.setArticleId(articleId)
                .setReplyerId(user.getUuid())
                .setContent(content)
                .setAuthorId(ar.getUserId())
                .setUuid(IdGenerator.uuid());
        // 为表情内容的设置
        this.arCommentDao.predo();
        if (this.arCommentDao.insert(co) > 0) {
            this.addCommentNum(articleId, true);
            // 别人评论的
            if (!co.getAuthorId().equals(co.getReplyerId())) {
                // 消息推送
                isSendMsg = true;
            }
        }
        final Map<String, String> map = new HashMap<>(8);
        map.put("replyerId", user.getUuid());
        map.put("replyerName", user.getNickname());
        map.put("replyerAvatar", user.getAvatar());
        map.put("content", content);
        map.put("createDate", TimeUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm"));

        if (isSendMsg) {
            this.messageService.sendMsgEvent(new Message()
                    .setSenderId(user.getUuid())
                    .setReceiverId(ar.getUserId())
                    .setType(MsgType.NEWS.statusCode())
                    .setBizId(articleId)
                    .setBizType(BizType.ARTICLE.statusCode())
                    .setBizAction(BizAction.COMMENT.statusCode())
                    .setContent(content)
                    .setReply(null));
        }
        return mapper.data(map).resultJson();
    }

    @Override
    public String reply(HttpServletRequest request, String commentId, String replyContent) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        if (StringUtil.isBlank(replyContent)) {
            return mapper.code(ResponseCode.ARGS_ERROR.statusCode()).resultJson();
        }
        final User user = UserUtils.checkLoginDead(request);
        if (user == null) {
            return mapper.code(ResponseCode.LOGIN_INVALIDATE.statusCode()).resultJson();
        }
        ArticleComment comment = this.arCommentDao.getByUuid(commentId);
        if (comment == null) {
            return mapper.code(ResponseCode.NO_DATA.statusCode()).resultJson();
        }
        ArticleComment reply = new ArticleComment();
        reply.setArticleId(comment.getArticleId())
                .setAuthorId(comment.getAuthorId())
                .setContent(replyContent)
                .setParentId(comment.getUuid())
                .setParentReplyerId(comment.getReplyerId())
                .setReplyerId(user.getUuid())
                .setUuid(IdGenerator.uuid());
        if (this.arCommentDao.insert(reply) > 0) {
            // 不能是自己回复自己
            if (!user.getUuid().equals(comment.getReplyerId())) {
                this.messageService.sendMsgEvent(new Message()
                        .setSenderId(user.getUuid())
                        .setReceiverId(null)
                        .setType(MsgType.NEWS.statusCode())
                        .setBizId(reply.getUuid())
                        .setBizType(BizType.USER.statusCode())
                        .setBizAction(BizAction.REPLY.statusCode())
                        .setContent(replyContent)
                        .setReply(null));
            }
        }
        return mapper.resultJson();
    }

    private void addCommentNum(String articleId, boolean flag) {
        ArticleAttr attr = new ArticleAttr();
        attr.setArticleId(articleId);
        final ArticleAttr at = this.articleAttrDao.getForUpdate(articleId);
        if (flag) {
            // 评论
            attr.setCommentNum(at.getCommentNum() + 1);
        } else {
            // 删除评论
            attr.setCommentNum(at.getCommentNum() - 1);
        }
        this.articleAttrDao.update(attr);
    }

    @Override
    public String comments(HttpServletRequest request, String articleId, Integer pageNum) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        PageHelper.startPage(pageNum, 10);
        Page<ArticleCommentVo> page = (Page<ArticleCommentVo>) this.arCommentDao.findList(articleId);
        final List<ArticleCommentVo> list = page.getResult();
        if (list == null || list.isEmpty()) {
            return mapper.code(ResponseCode.NO_DATA.statusCode()).resultJson();
        }
        // 是否登录
        boolean isLogin = (UserUtils.checkLoginDead(request) != null);
        String tuserId = request.getHeader("userId");
        List<Map<String, Object>> total = this.handleCommentsList(list, isLogin, tuserId);
        return mapper.count(page.getTotal()).data(total).resultJson();
    }

    private List<Map<String, Object>> handleCommentsList(List<ArticleCommentVo> list, final boolean isLogin,
            final String tuserId) {
        CommentLike t = null;
        List<CommentLike> likeQueryList = new ArrayList<>(list.size());
        if (isLogin) {
            for (final ArticleCommentVo a : list) {
                t = new CommentLike();
                t.setUserId(tuserId).setCommentId(a.getUuid());
                likeQueryList.add(t);
            }
        }
        List<CommentLike> likeList = null;
        if (!likeQueryList.isEmpty()) {
            likeList = this.arCommentDao.getLikes(likeQueryList);
        }

        Map<String, Object> map = null;
        List<Map<String, Object>> total = new ArrayList<>();
        for (final ArticleCommentVo a : list) {
            map = MapleUtil.wrap(a)
                    .rename("id", "commentId")
                    .stick("createDate", TimeUtils.format(a.getCreateDate(), "yyyy-MM-dd HH:mm"))
                    .stick("isLike", "0")
                    .map();
            if (!isLogin) {
                continue;
            }
            if (likeList != null && !likeList.isEmpty()) {
                for (CommentLike c : likeList) {
                    if (c.getDelFlag() != 1 && c.getCommentId().equals(a.getUuid())) {
                        map.put("isLike", c.getStatus() + "");
                        c.setDelFlag(1);
                        break;
                    }
                }
            }
            total.add(map);
        }
        return total;
    }

    @Override
    public String newComments(HttpServletRequest request, String articleId) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        final List<ArticleCommentVo> list = this.arCommentDao.findNewComments(articleId);
        if (list == null || list.isEmpty()) {
            return mapper.code(ResponseCode.NO_DATA.statusCode()).resultJson();
        }
        // 是否登录
        boolean isLogin = (UserUtils.checkLoginDead(request) != null);
        String tuserId = request.getHeader("userId");
        List<Map<String, Object>> total = this.handleCommentsList(list, isLogin, tuserId);
        return mapper.data(total).resultJson();
    }

    // =============================评论相关===========================//
    @Override
    public String addCommentLike(HttpServletRequest request, String commentId, Integer isLike) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        // 没登录 或失效
        if (UserUtils.checkLoginDead(request) == null) {
            if (isLike == 0) {
                this.addCommentLikeNum(new CommentLike().setCommentId(commentId), true);
            } else if (isLike == 1) {
                this.addCommentLikeNum(new CommentLike().setCommentId(commentId), false);
            }
            return mapper.code(ResponseCode.LOGIN_INVALIDATE.statusCode()).resultJson();
        }

        final CommentLike t = new CommentLike();
        t.setUserId(request.getHeader("userId")).setCommentId(commentId);
        this.handleLikeOrCollectNum(t);

        return mapper.resultJson();
    }

    @Transactional
    private <T> void handleLikeOrCollectNum(T t) {
        final Message msg = new Message()
                .setReceiverId(null)
                .setContent(null)
                .setReply(null);
        boolean isSendMsg = false;
        if (t instanceof CommentLike) {
            CommentLike c = (CommentLike) t;
            msg.setSenderId(c.getUserId())
                    .setType(MsgType.NEWS.statusCode())
                    .setBizId(c.getCommentId())
                    .setBizType(BizType.ARTICLE.statusCode())
                    .setBizAction(BizAction.COMMENT_LIKE.statusCode());
            final CommentLike like = this.arCommentDao.getLikeForUpdate(c);
            if (like != null) {
                if (like.getStatus() == 1) {
                    // 取消点赞
                    this.addCommentLikeNum(c.setStatus(0), false);
                } else {
                    // 进行点赞
                    this.addCommentLikeNum(c.setStatus(1), true);
                }
                if (this.arCommentDao.updateLike(c) > 0 && like.getStatus() == 0) {
                    isSendMsg = true;
                }
            } else if (this.arCommentDao.insertLike(c) > 0 && this.addCommentLikeNum(c, true) > 0) {
                // 没点过赞
                isSendMsg = true;
            }
        } else if (t instanceof ArticleLike) {
            ArticleLike a = (ArticleLike) t;
            msg.setSenderId(a.getUserId())
                    .setType(MsgType.NEWS.statusCode())
                    .setBizId(a.getArticleId())
                    .setBizType(BizType.ARTICLE.statusCode())
                    .setBizAction(BizAction.ARTICLE_LIKE.statusCode());
            // 已经点过
            final ArticleLike like = this.likeDao.getForUpdate(a);
            if (like != null) {
                // 取消点赞
                if (like.getStatus() == 1) {
                    this.addLikeNum(a.setStatus(0), false);
                } else {
                    // 进行点赞
                    this.addLikeNum(a.setStatus(1), true);
                }
                if (this.likeDao.update(a) > 0 && like.getStatus() == 0) {
                    isSendMsg = true;
                }
            } else if (this.likeDao.insert(a.setNum(1)) > 0) {
                // 没点过赞
                this.addLikeNum(a, true);
                isSendMsg = true;
            }

        } else if (t instanceof ArticleCollect) {
            ArticleCollect a = (ArticleCollect) t;
            msg.setSenderId(a.getUserId())
                    .setType(MsgType.NEWS.statusCode())
                    .setBizId(a.getArticleId())
                    .setBizType(BizType.ARTICLE.statusCode())
                    .setBizAction(BizAction.COLLECT.statusCode());
            // 已经收藏
            final ArticleCollect co = this.collectDao.getForUpdate(a);
            if (co != null) {
                // 取消收藏
                if (co.getStatus() == 1) {
                    this.addCollectNum(a.setStatus(0), false);
                } else {
                    // 进行收藏
                    this.addCollectNum(a.setStatus(1), true);
                }
                if (this.collectDao.update(a) > 0 && co.getStatus() == 0) {
                    isSendMsg = true;
                }
            } else {
                // 没收藏
                a.setUuid(IdGenerator.uuid());
                if (this.collectDao.insert(a) > 0 && this.addCollectNum(a, true) > 0) {
                    // 消息推送
                    isSendMsg = true;
                }
            }
        }

        if (isSendMsg) {
            this.messageService.sendMsgEvent(msg);
        }
    }

    private int addCommentLikeNum(CommentLike c, boolean flag) {
        ArticleComment temp = new ArticleComment();
        temp.setUuid(c.getCommentId());
        ArticleComment ac = this.arCommentDao.getForUpdate(c.getCommentId());
        if (flag) {// 点赞
            temp.setNum(ac.getNum() + 1);
        } else {
            temp.setNum(ac.getNum() - 1);
        }
        return this.arCommentDao.update(temp);
    }

    @Override
    public String latestOfUsers(HttpServletRequest request, String[] userIds) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        List<ArticleVo> list = this.articleDao.findLatestOfUsers(userIds);
        if (list != null && !list.isEmpty()) {
            mapper.data(list);
        }
        return mapper.resultJson();
    }

    @Override
    public String search(HttpServletRequest request, String keyword) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        final Map<String, Object> map = ElasticUtils.searchWithCount(new String[] { "website" },
                new String[] { "article" }, 0, 10, keyword, new String[] { "title", "content" });
        return mapper.data(map).resultJson();
    }

    @Override
    public void synElastic() {
        int pageSize = 100;
        Page<Article> page = new Page<>();
        int count = this.articleDao.count();
        int pageNum = (count + pageSize - 1) / pageSize;
        List<Article> list = null;
        Map<String, String> jsonMap = null;
        // 分页同步 防止一次性取出量过大
        int i = 1;
        while (i <= pageNum) {
            PageHelper.startPage(i++, pageSize, true);
            page = (Page<Article>) this.articleDao.findByList(new Article());
            if (page != null && page.getResult() != null && !page.getResult().isEmpty()) {
                list = page.getResult();
                jsonMap = new HashMap<>(134);
                for (Article a : list) {
                    jsonMap.put(a.getUuid(), JSON.toJSONString(a));
                    ElasticUtils.upsertList("website", "article", jsonMap);
                }
            }
            LOG.info("同步成功");
            return;
        }
        LOG.info("同步失败");
    }

}
