/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.article.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xiaoyu.beacon.autoconfigure.anno.BeaconExporter;
import com.xiaoyu.common.base.ResponseCode;
import com.xiaoyu.common.base.ResponseMapper;
import com.xiaoyu.common.request.TraceRequest;
import com.xiaoyu.common.utils.ElasticUtils;
import com.xiaoyu.common.utils.IdGenerator;
import com.xiaoyu.common.utils.JedisUtils;
import com.xiaoyu.common.utils.SpringBeanUtils;
import com.xiaoyu.common.utils.StringUtil;
import com.xiaoyu.common.utils.TimeUtils;
import com.xiaoyu.core.constant.Type;
import com.xiaoyu.core.template.BaseProducer;
import com.xiaoyu.maple.core.MapleUtil;
import com.xiaoyu.modules.biz.article.dao.ArticleAttrDao;
import com.xiaoyu.modules.biz.article.dao.ArticleCollectDao;
import com.xiaoyu.modules.biz.article.dao.ArticleColumnDao;
import com.xiaoyu.modules.biz.article.dao.ArticleCommentDao;
import com.xiaoyu.modules.biz.article.dao.ArticleDao;
import com.xiaoyu.modules.biz.article.dao.ArticleLikeDao;
import com.xiaoyu.modules.biz.article.entity.Article;
import com.xiaoyu.modules.biz.article.entity.ArticleAttr;
import com.xiaoyu.modules.biz.article.entity.ArticleCollect;
import com.xiaoyu.modules.biz.article.entity.ArticleColumn;
import com.xiaoyu.modules.biz.article.entity.ArticleComment;
import com.xiaoyu.modules.biz.article.entity.ArticleLike;
import com.xiaoyu.modules.biz.article.entity.CommentLike;
import com.xiaoyu.modules.biz.article.service.api.IArticleService;
import com.xiaoyu.modules.biz.article.vo.ArticleCommentVo;
import com.xiaoyu.modules.biz.article.vo.ArticleVo;
import com.xiaoyu.modules.biz.message.entity.Message;
import com.xiaoyu.modules.biz.message.service.api.IMessageService;
import com.xiaoyu.modules.biz.user.entity.User;
import com.xiaoyu.modules.biz.user.service.api.IUserService;
import com.xiaoyu.modules.biz.user.vo.UserVo;
import com.xiaoyu.modules.common.MailBuilder;
import com.xiaoyu.modules.constant.BizAction;
import com.xiaoyu.modules.constant.BizType;
import com.xiaoyu.modules.constant.Flag;
import com.xiaoyu.modules.constant.MqContant;
import com.xiaoyu.modules.constant.MsgType;
import com.xiaoyu.modules.constant.NumCountType;

/**
 * @author xiaoyu 2016年3月29日
 *         使用cglib进行代理,来使用事务的跨方法调用
 */
@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
@Primary
@BeaconExporter(interfaceName = "com.xiaoyu.modules.biz.article.service.api.IArticleService", group = "dev")
public class ArticleServiceImpl implements IArticleService {

    private static final Logger LOG = LoggerFactory.getLogger(ArticleServiceImpl.class);

    @Autowired
    private IUserService userService;
    @Autowired
    private ArticleDao articleDao;
    @Autowired
    private ArticleAttrDao articleAttrDao;
    @Autowired
    private ArticleCollectDao collectDao;
    @Autowired
    private ArticleCommentDao arCommentDao;
    @Autowired
    private ArticleLikeDao likeDao;

    @Autowired
    private ArticleColumnDao columnDao;

    @Autowired(required = false)
    private IMessageService messageService;

    private Map<String, Object> article2Map(final ArticleVo a) {
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
        UserVo vo = this.userService.getVoByUuid(a.getUserId());
        Map<String, Object> result = MapleUtil.wrap(a)
                .rename("uuid", "articleId")
                .stick("createDate", TimeUtils.format(a.getCreateDate(), "yyyy-MM-dd"))
                .stick("createTime", TimeUtils.format(a.getCreateDate(), "HH:mm"))
                .stick("user", vo)
                .map();
        return mapper.data(result).resultJson();
    }

    @Override
    public String hotList(TraceRequest request) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        // if (EhCacheUtil.IsExist("SystemCache")) {// 从缓存取
        // @SuppressWarnings("unchecked")
        // List<Object> total = (List<Object>) EhCacheUtil.get("SystemCache",
        // "article:hotList");
        // if (total != null && total.size() > 0) {
        // return mapper.data(total).resultJson();
        // }
        // }
        int pageNum = Integer.valueOf(request.getHeader().getPageNum());
        int pageSize = Integer.valueOf(request.getHeader().getPageSize());
        pageSize = pageSize > 32 ? 32 : pageSize;
        PageHelper.startPage(pageNum, pageSize);
        List<ArticleVo> list = this.articleDao.findHotList();

        List<Map<String, Object>> total = this.handleArticleVoList(list, request.getUser(), request.isLogin());
        // EhCacheUtil.put("SystemCache", "article:hotList", total);// 存入缓存
        return mapper.data(total).resultJson();
    }

    @Override
    public String list(TraceRequest request, String userId) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        final Article article = new Article();
        article.setUserId(userId);
        int pageNum = Integer.valueOf(request.getHeader().getPageNum());
        int pageSize = Integer.valueOf(request.getHeader().getPageSize());
        pageSize = pageSize > 32 ? 32 : pageSize;
        PageHelper.startPage(pageNum, pageSize);
        List<ArticleVo> list = this.articleDao.findByListWithAttr(userId);
        if (list.isEmpty()) {
            return mapper.resultJson();
        }
        List<Map<String, Object>> total = this.handleArticleVoList(list, request.getUser(), request.isLogin());

        return mapper.data(total).resultJson();
    }

    @Override
    public String collectList(TraceRequest request, String userId) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        // 是否登录
        final boolean isLogin = request.isLogin();
        final Article article = new Article();
        article.setUserId(userId);
        int pageNum = Integer.valueOf(request.getHeader().getPageNum());
        int pageSize = Integer.valueOf(request.getHeader().getPageSize());
        pageSize = pageSize > 32 ? 32 : pageSize;

        PageHelper.startPage(pageNum, pageSize);
        final List<ArticleVo> list = this.articleDao.findCollectList(userId);
        List<Map<String, Object>> total = this.handleArticleVoList(list, request.getUser(), isLogin);

        return mapper.data(total).resultJson();
    }

    private List<Map<String, Object>> handleArticleVoList(final List<ArticleVo> list, final User user,
            final boolean isLogin) {
        int size = list.size();
        if (size == 0) {
            return new ArrayList<>(0);
        }
        final List<Map<String, Object>> total = new ArrayList<>(size);
        List<String> userIdList = new ArrayList<>(size);
        List<ArticleLike> likeList = null;
        List<ArticleCollect> collectList = null;
        if (isLogin) {
            List<ArticleLike> likeQueryList = new ArrayList<>(size);
            List<ArticleCollect> collectQueryList = new ArrayList<>(size);
            ArticleLike t = new ArticleLike();
            ArticleCollect t1 = new ArticleCollect();
            // 设置查询条件
            for (ArticleVo a : list) {
                t = new ArticleLike();
                t.setUserId(user.getUuid()).setArticleId(a.getUuid());
                t1 = new ArticleCollect();
                t1.setUserId(user.getUuid()).setArticleId(a.getUuid());
                userIdList.add(a.getUserId());
                likeQueryList.add(t);
                collectQueryList.add(t1);
            }
            likeList = this.likeDao.findListByBatch(likeQueryList);
            collectList = this.collectDao.findListByBatch(collectQueryList);
        }
        // 批量查询
        List<UserVo> userVoList = null;
        if (!userIdList.isEmpty()) {
            userVoList = this.userService.findVoByUuid(userIdList);
        }
        // 封装数据
        Map<String, UserVo> voMap = new HashMap<>();
        if (userVoList != null) {
            for (UserVo u : userVoList) {
                voMap.put(u.getUserId(), u);
            }
        }
        Map<String, ArticleLike> likeMap = new HashMap<>(16);
        if (likeList != null && !likeList.isEmpty()) {
            for (ArticleLike li : likeList) {
                likeMap.put(li.getArticleId(), li);
            }
        }
        Map<String, ArticleCollect> coMap = new HashMap<>(16);
        if (collectList != null && !collectList.isEmpty()) {
            for (ArticleCollect c : collectList) {
                coMap.put(c.getArticleId(), c);
            }
        }
        Map<String, Object> map = null;
        UserVo u = null;
        ArticleLike li = null;
        ArticleCollect c = null;
        String tarticleId1 = null;
        // 处理数据
        for (ArticleVo a : list) {
            tarticleId1 = a.getUuid();
            u = voMap.get(a.getUserId());
            if (u != null) {
                a.setUser(u);
            }
            map = this.article2Map(a);
            if (!isLogin) {
                total.add(map);
                continue;
            }
            li = likeMap.get(tarticleId1);
            if (li != null) {
                map.put("isLike", Integer.toString(li.getStatus()));
            }
            c = coMap.get(tarticleId1);
            if (c != null) {
                map.put("isCollect", Integer.toString(c.getStatus()));
            }
            total.add(map);
        }
        return total;
    }

    @Override
    public String addArticle(TraceRequest request, String title, String content) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        if (!request.isLogin()) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.LOGIN_INVALIDATE.statusCode())
                    .message("登录失效,请重新登录")
                    .resultJson();
        }
        User user = request.getUser();
        Article t = new Article();
        ArticleAttr attr = new ArticleAttr();
        t.setContent(content)
                .setTitle(title)
                .setUserId(user.getUuid())
                .setUuid(IdGenerator.uuid());
        attr.setArticleId(t.getUuid())
                .setUuid(IdGenerator.uuid());

        SpringBeanUtils.getBean(ArticleServiceImpl.class)
                .doInsertArticle(t, attr, user.getUuid());
        return mapper.data(t.getUuid()).resultJson();
    }

    @Transactional(readOnly = false, rollbackFor = RuntimeException.class)
    public void doInsertArticle(final Article t, final ArticleAttr attr, final String userId) {
        try {
            this.articleDao.insert(t);
            this.articleAttrDao.insert(attr);
            // 增加发文数
            this.userService.addNum(NumCountType.ArticleNum.ordinal(), 1, userId);
        } catch (RuntimeException e) {
            LOG.error("publish artile failed,then rollback.", e);
            throw e;
        }
        // 通知我有人发文章了,哈哈
        MailBuilder builder = new MailBuilder();
        builder.sender("1546428286@qq.com", "小往")
                .receiver("1546428286@qq.com", "Mr xiaoyu")
                .title("往往:发表通知.")
                .content("新鲜出炉!<br/>名称:" + t.getTitle()
                        + "<br/>速速去了解一下拉"
                        + "<br/><a href='http://47.93.235.211/article/" + t.getUuid() + "'>这也是个神奇的链接...</a>");
        try {
            BaseProducer.produce(Type.QUEUE, MqContant.EMAIL, JSON.toJSONString(builder));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public String editArticle(TraceRequest request, String title, String content, String userId,
            String articleId) {
        if (!request.isLogin()) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.LOGIN_INVALIDATE.statusCode())
                    .message("登录失效,请重新登录")
                    .resultJson();
        }
        return this.doEdit(userId, title, content, articleId);
    }

    private String doEdit(String userId, String title, String content, String articleId) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        Article ar = articleDao.getByUuid(articleId);
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
    public String addReadNum(TraceRequest request, String articleId) {
        String ip = request.getHeader().getRemoteHost();
        if (JedisUtils.get("user:login:" + ip) != null) {
            return ResponseMapper.createMapper().resultJson();
        }
        ArticleAttr temp = new ArticleAttr();
        temp.setReadNum(1)
                .setArticleId(articleId);
        this.articleAttrDao.updateByAddition(temp);
        JedisUtils.set("user:login:" + ip, temp.getReadNum().toString(), 600);
        return ResponseMapper.createMapper().data(temp.getReadNum()).resultJson();
    }

    @Override
    public String addLike(TraceRequest request, String articleId, Integer isLike) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        if (this.articleDao.isExist(articleId) < 1) {
            return mapper.code(ResponseCode.ARGS_ERROR.statusCode()).resultJson();
        }
        ArticleLike t = new ArticleLike();
        t.setUserId(request.getUser().getUuid())
                .setArticleId(articleId);
        // 没登录 或失效
        if (!request.isLogin()) {
            if (isLike == 0) {
                this.addLikeNum(t, false);
            } else if (isLike == 1) {
                this.addLikeNum(t, true);
            }
            return mapper.code(ResponseCode.LOGIN_INVALIDATE.statusCode()).resultJson();
        }

        SpringBeanUtils.getBean(ArticleServiceImpl.class)
                .handleLikeOrCollectNum(t);
        return mapper.resultJson();

    }

    private void addLikeNum(ArticleLike a, boolean flag) {
        ArticleAttr temp = new ArticleAttr();
        temp.setArticleId(a.getArticleId());
        if (flag) {
            // 点赞
            temp.setLikeNum(1);
        } else {
            temp.setLikeNum(-1);
        }
        this.articleAttrDao.updateByAddition(temp);
    }

    /**
     * isCollect 0取消收藏 1收藏
     */
    @Override
    public String addCollect(TraceRequest request, String articleId, Integer isCollect) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        // 没登录 或失效
        if (!request.isLogin()) {
            return mapper.code(ResponseCode.LOGIN_INVALIDATE.statusCode()).resultJson();
        }
        if (this.articleDao.isExist(articleId) < 1) {
            return mapper.code(ResponseCode.ARGS_ERROR.statusCode()).resultJson();
        }
        ArticleCollect t = new ArticleCollect();
        t.setUserId(request.getUser().getUuid()).setArticleId(articleId);
        SpringBeanUtils.getBean(ArticleServiceImpl.class)
                .handleLikeOrCollectNum(t);
        return mapper.resultJson();

    }

    private int addCollectNum(ArticleCollect a, boolean flag) {
        ArticleAttr temp = new ArticleAttr();
        temp.setArticleId(a.getArticleId());
        // 收藏
        if (flag) {
            temp.setCollectNum(1);
        } else {
            temp.setCollectNum(-1);
        }
        this.articleAttrDao.updateByAddition(temp);
        if (flag) {
            this.userService.addNum(NumCountType.CollectNum.ordinal(), 1, a.getUserId());
        } else {
            this.userService.addNum(NumCountType.CollectNum.ordinal(), -1, a.getUserId());
        }
        return 1;
    }

    @Override
    public String comment(TraceRequest request, String articleId, String content) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        if (StringUtil.isEmpty(content)) {
            return mapper.code(ResponseCode.ARGS_ERROR.statusCode()).resultJson();
        }
        if (!request.isLogin()) {
            return mapper.code(ResponseCode.LOGIN_INVALIDATE.statusCode()).resultJson();
        }
        final User user = request.getUser();
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
    public String reply(TraceRequest request, String commentId, String replyContent) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        if (StringUtil.isEmpty(replyContent)) {
            return mapper.code(ResponseCode.ARGS_ERROR.statusCode()).resultJson();
        }
        if (!request.isLogin()) {
            return mapper.code(ResponseCode.LOGIN_INVALIDATE.statusCode()).resultJson();
        }
        final User user = request.getUser();
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
        ArticleAttr temp = new ArticleAttr();
        temp.setArticleId(articleId);
        if (flag) {
            // 评论
            temp.setCommentNum(1);
        } else {
            // 删除评论
            temp.setCommentNum(-1);
        }
        this.articleAttrDao.updateByAddition(temp);
    }

    @Override
    public String comments(TraceRequest request, String articleId) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        int pageNum = Integer.valueOf(request.getHeader().getPageNum());
        int pageSize = Integer.valueOf(request.getHeader().getPageSize());
        pageSize = pageSize > 32 ? 32 : pageSize;
        Page<?> page = PageHelper.startPage(pageNum, pageSize);
        final List<ArticleCommentVo> list = this.arCommentDao.findList(articleId);
        if (list.isEmpty()) {
            return mapper.code(ResponseCode.NO_DATA.statusCode()).resultJson();
        }
        List<Map<String, Object>> total = this.handleCommentsList(list, request.getUser(), request.isLogin());
        return mapper.count(page.getTotal()).data(total).resultJson();
    }

    private List<Map<String, Object>> handleCommentsList(List<ArticleCommentVo> list,
            final User user, final boolean isLogin) {
        int size = list.size();
        List<CommentLike> likeQueryList = new ArrayList<>(size);
        // 查询条件
        if (isLogin) {
            for (final ArticleCommentVo a : list) {
                likeQueryList.add(new CommentLike()
                        .setUserId(user.getUuid())
                        .setCommentId(a.getUuid()));
            }
        }
        // 查询数据
        List<CommentLike> likeList = null;
        if (!likeQueryList.isEmpty()) {
            likeList = this.arCommentDao.getLikes(likeQueryList);
        }
        // 封装数据
        Map<String, CommentLike> likeMap = new HashMap<>(16);
        if (likeList != null && !likeList.isEmpty()) {
            for (CommentLike c : likeList) {
                likeMap.put(c.getCommentId(), c);
            }
        }
        // 处理数据
        Map<String, Object> map = null;
        List<Map<String, Object>> total = new ArrayList<>(size);
        CommentLike c = null;
        for (final ArticleCommentVo a : list) {
            map = MapleUtil.wrap(a)
                    .rename("uuid", "commentId")
                    .stick("createDate", TimeUtils.format(a.getCreateDate(), "yyyy-MM-dd HH:mm"))
                    .stick("isLike", "0")
                    .map();
            if (!isLogin) {
                total.add(map);
                continue;
            }
            c = likeMap.get(a.getUuid());
            if (c != null) {
                map.put("isLike", Integer.toString(c.getStatus()));
            }
            total.add(map);
        }
        return total;
    }

    @Override
    public String newComments(TraceRequest request, String articleId) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        final List<ArticleCommentVo> list = this.arCommentDao.findNewComments(articleId);
        if (list == null || list.isEmpty()) {
            return mapper.code(ResponseCode.NO_DATA.statusCode()).resultJson();
        }
        List<Map<String, Object>> total = this.handleCommentsList(list, request.getUser(), request.isLogin());
        return mapper.data(total).resultJson();
    }

    /**
     * =============================评论相关===========================
     */

    @Override
    public String addCommentLike(TraceRequest request, String commentId, Integer isLike) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        // 没登录 或失效
        if (!request.isLogin()) {
            if (isLike == 0) {
                this.addCommentLikeNum(new CommentLike().setCommentId(commentId), false);
            } else if (isLike == 1) {
                this.addCommentLikeNum(new CommentLike().setCommentId(commentId), true);
            }
            return mapper.code(ResponseCode.LOGIN_INVALIDATE.statusCode()).resultJson();
        }

        final CommentLike t = new CommentLike();
        t.setUserId(request.getUser().getUuid()).setCommentId(commentId);
        SpringBeanUtils.getBean(ArticleServiceImpl.class)
                .handleLikeOrCollectNum(t);

        return mapper.resultJson();
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public <T> void handleLikeOrCollectNum(T t) {
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
        ArticleComment ac = this.arCommentDao.getByUuid(temp.getUuid());
        if (flag) {
            // 点赞
            temp.setNum(ac.getNum() + 1);
        } else {
            temp.setNum(ac.getNum() - 1);
        }
        temp.setOld(ac.getNum());
        // 进行乐观更新,最大重试1000次.可能会造成数据库连接量变大.
        int retry = 0;
        while (retry < 1000 && this.arCommentDao.updateOptimistic(temp) <= 0) {
            ac = this.arCommentDao.getByUuid(temp.getUuid());
            if (flag) {
                temp.setNum(ac.getNum() + 1);
            } else {
                temp.setNum(ac.getNum() - 1);
            }
            temp.setOld(ac.getNum());
            retry++;
        }
        if (retry > 0) {
            LOG.info("乐观锁更新操作,重试了{}次", retry);
        }
        return 1;
    }

    @Override
    public String latestOfUsers(TraceRequest request, String[] userIds) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        List<ArticleVo> list = this.articleDao.findLatestOfUsers(userIds);
        if (list != null && !list.isEmpty()) {
            mapper.data(list);
        }
        return mapper.resultJson();
    }

    @Override
    public String search(TraceRequest request, String keyword) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        final Map<String, Object> map = ElasticUtils.searchWithCount(new String[] { "website" },
                new String[] { "article" }, 0, 10, keyword, new String[] { "title", "content" });
        return mapper.data(map).resultJson();
    }

    @Override
    public void synElastic() {
        int pageSize = 96;
        int count = this.articleDao.count();
        int pageNum = (count + pageSize - 1) / pageSize;
        List<Article> list = null;
        Map<String, String> jsonMap = null;
        // 分页同步 防止一次性取出量过大
        int i = 1;
        Article t = new Article();
        while (i <= pageNum) {
            PageHelper.startPage(i++, pageSize, true);
            list = this.articleDao.findByList(t);
            if (!list.isEmpty()) {
                jsonMap = new HashMap<>(128);
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

    /* ==========================分类栏目相关=============================== */
    @Override
    public String addColumn(TraceRequest request, String name) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        if (!request.isLogin()) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.LOGIN_INVALIDATE.statusCode())
                    .message("登录失效,请重新登录")
                    .resultJson();
        }
        User user = request.getUser();
        if (StringUtil.isBlank(name) || name.length() > 10) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.ARGS_ERROR.statusCode())
                    .message("请正确填写名称")
                    .resultJson();
        }

        ArticleColumn column = new ArticleColumn();
        column
                .setUserId(user.getUuid());
        if (this.columnDao.count(column) > 50) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.FAILED.statusCode())
                    .message("我们觉得太多栏目可能并不是那么美好")
                    .resultJson();
        }
        column.setName(name)
                .setUuid(IdGenerator.uuid());
        this.columnDao.insert(column);
        return mapper.data(column).resultJson();

    }

    @Override
    public String removeColumn(TraceRequest request, String columnId) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        if (!request.isLogin()) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.LOGIN_INVALIDATE.statusCode())
                    .message("登录失效,请重新登录")
                    .resultJson();
        }
        User user = request.getUser();
        ArticleColumn t = new ArticleColumn();
        t.setUserId(user.getUuid()).setUuid(columnId);
        this.columnDao.delete(t);
        return mapper.resultJson();
    }

    @Override
    public String updateColumn(TraceRequest request, String columnId, String name) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        if (!request.isLogin()) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.LOGIN_INVALIDATE.statusCode())
                    .message("登录失效,请重新登录")
                    .resultJson();
        }
        User user = request.getUser();
        ArticleColumn t = new ArticleColumn();
        t.setUserId(user.getUuid())
                .setName(name)
                .setUuid(columnId);
        this.columnDao.update(t);
        return mapper.resultJson();
    }

    @Override
    public String putOrTakeColumn(TraceRequest request, String columnId, String articleId) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        if (!request.isLogin()) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.LOGIN_INVALIDATE.statusCode())
                    .message("登录失效,请重新登录")
                    .resultJson();
        }

        User user = request.getUser();
        Article t = new Article();
        t.setUserId(user.getUuid())
                .setUuid(articleId);
        Article article = this.articleDao.getByUuid(columnId);
        if (article == null) {
            return mapper.code(ResponseCode.NO_DATA.statusCode()).resultJson();
        }
        if (!article.getUserId().equals(user.getUuid())) {
            return mapper.code(ResponseCode.NO_DATA.statusCode()).resultJson();
        }
        if (this.columnDao.isExist(columnId) <= 0) {
            return mapper.code(ResponseCode.NO_DATA.statusCode()).resultJson();
        }
        if ("".equals((columnId)) && StringUtil.isBlank(article.getColumnId())) {
            return mapper.resultJson();
        }
        t.setColumnId(columnId);
        this.articleDao.update(t);
        return mapper.resultJson();
    }

    @Override
    public String columns(TraceRequest request, String userId) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        User user = request.getUser();
        ArticleColumn column = new ArticleColumn();
        column.setUserId(userId);
        if (user == null || !user.getUuid().equals(userId)) {
            column.setIsOpen(Flag.True.ordinal());
        }
        List<ArticleColumn> columnList = this.columnDao.findByList(column);
        List<Map<String, Object>> total = this.handleColumnList(columnList);
        return mapper.data(total).resultJson();
    }

    private List<Map<String, Object>> handleColumnList(List<ArticleColumn> columnList) {
        if (columnList == null || columnList.isEmpty()) {
            return new ArrayList<>(0);
        }
        List<Map<String, Object>> total = new ArrayList<>();
        for (ArticleColumn cu : columnList) {
            total.add(MapleUtil.wrap(cu).rename("uuid", "columnId")
                    .skip("id", "delFlag", "createDate", "updateDate").map());
        }
        return total;
    }

    @Override
    public String findListByColumn(TraceRequest request, String userId, String columnId) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        // 是否登录
        User user = request.getUser();
        boolean isLogin = request.isLogin();
        ArticleColumn co = new ArticleColumn().setUserId(userId);
        co.setUuid(columnId);
        int pageNum = Integer.valueOf(request.getHeader().getPageNum());
        int pageSize = Integer.valueOf(request.getHeader().getPageSize());
        pageSize = pageSize > 32 ? 32 : pageSize;
        List<ArticleVo> list = null;
        if (user == null) {
            // 没有登录
            co.setIsOpen(Flag.True.ordinal());
        } else {
            // 登录,但看的不是自己的
            if (!user.getUuid().equals(userId)) {
                co.setIsOpen(Flag.True.ordinal());
            }
        }
        PageHelper.startPage(pageNum, pageSize);
        list = this.articleDao.findByColumn(co);
        if (list.isEmpty()) {
            return mapper.resultJson();
        }
        List<Map<String, Object>> total = this.handleArticleVoList(list, request.getUser(), isLogin);

        return mapper.data(total).resultJson();
    }

    @Override
    public Article getByUuid(String uuid) {
        return this.articleDao.getByUuid(uuid);
    }

}
