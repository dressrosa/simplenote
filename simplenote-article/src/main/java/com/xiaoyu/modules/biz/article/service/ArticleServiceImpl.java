/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.article.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import com.xiaoyu.common.base.CommonQuery;
import com.xiaoyu.common.base.ResponseCode;
import com.xiaoyu.common.base.ResponseMapper;
import com.xiaoyu.common.request.TraceRequest;
import com.xiaoyu.common.utils.IdGenerator;
import com.xiaoyu.common.utils.JedisUtils;
import com.xiaoyu.common.utils.RedisLock;
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
                .stick("content", a.getContent().length() > 60 ? a.getContent().substring(0, 59) : a.getContent())
                .stick("createDate", TimeUtils.format(a.getCreateDate(), "yyyy-MM-dd HH:mm"))
                .stick("isLike", "0")
                .stick("isCollect", "0")
                .map();
    }

    @Override
    public ResponseMapper detail(String articleId) {
        ArticleVo a = this.articleDao.getVoByUuid(articleId);
        if (a == null) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.NO_DATA.statusCode());
        }
        UserVo vo = this.userService.getVoByUuid(a.getUserId());
        Map<String, Object> result = MapleUtil.wrap(a)
                .rename("uuid", "articleId")
                .stick("createDate", TimeUtils.format(a.getCreateDate(), "yyyy-MM-dd"))
                .stick("createTime", TimeUtils.format(a.getCreateDate(), "HH:mm"))
                .stick("user", vo)
                .map();
        return ResponseMapper.createMapper().data(result);
    }

    @Override
    public ResponseMapper hotList(TraceRequest request) {
        // if (EhCacheUtil.IsExist("SystemCache")) {// 从缓存取
        // @SuppressWarnings("unchecked")
        // List<Object> total = (List<Object>) EhCacheUtil.get("SystemCache",
        // "article:hotList");
        // if (total != null && total.size() > 0) {
        // return mapper.data(total);
        // }
        // }
        int pageNum = request.getHeader().getPageNum();
        int pageSize = request.getHeader().getPageSize();
        pageSize = pageSize > 32 ? 32 : pageSize;

        PageHelper.startPage(pageNum, pageSize);
        List<ArticleVo> list = this.articleDao.findHotList();

        List<Map<String, Object>> total = this.handleArticleVoList(list, request.getUser(), request.isLogin());
        // EhCacheUtil.put("SystemCache", "article:hotList", total);// 存入缓存
        return ResponseMapper.createMapper().data(total);
    }

    @Override
    public ResponseMapper list(TraceRequest request, String userId) {
        final Article article = new Article();
        article.setUserId(userId);
        int pageNum = request.getHeader().getPageNum();
        int pageSize = request.getHeader().getPageSize();
        pageSize = pageSize > 32 ? 32 : pageSize;

        PageHelper.startPage(pageNum, pageSize);
        List<ArticleVo> list = this.articleDao.findByListWithAttr(userId);
        if (list.isEmpty()) {
            return ResponseMapper.createMapper();
        }
        List<Map<String, Object>> total = this.handleArticleVoList(list, request.getUser(), request.isLogin());
        return ResponseMapper.createMapper().data(total);
    }

    @Override
    public ResponseMapper collectList(TraceRequest request, String userId) {
        // 是否登录
        final boolean isLogin = request.isLogin();
        final Article article = new Article();
        article.setUserId(userId);
        int pageNum = request.getHeader().getPageNum();
        int pageSize = request.getHeader().getPageSize();
        pageSize = pageSize > 32 ? 32 : pageSize;

        PageHelper.startPage(pageNum, pageSize);
        final List<ArticleVo> list = this.articleDao.findCollectList(userId);

        List<Map<String, Object>> total = this.handleArticleVoList(list, request.getUser(), isLogin);
        return ResponseMapper.createMapper().data(total);
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
                likeQueryList.add(t);
                collectQueryList.add(t1);
            }
            likeList = this.likeDao.findListByBatch(likeQueryList);
            collectList = this.collectDao.findListByBatch(collectQueryList);
        }
        list.forEach(a -> {
            userIdList.add(a.getUserId());
        });
        // 批量查询
        List<UserVo> userVoList = null;
        if (!userIdList.isEmpty()) {
            userVoList = this.userService.findVoByUuid(userIdList);
        }
        // 封装数据
        Map<String, UserVo> voMap = new HashMap<>(16);
        Optional.ofNullable(userVoList).ifPresent(a -> {
            a.forEach(u -> {
                voMap.put(u.getUserId(), u);
            });
        });
        Map<String, ArticleLike> likeMap = new HashMap<>(16);
        Optional.ofNullable(likeList).ifPresent(a -> {
            a.forEach(li -> {
                likeMap.put(li.getArticleId(), li);
            });
        });
        Map<String, ArticleCollect> coMap = new HashMap<>(16);
        Optional.ofNullable(collectList).ifPresent(a -> {
            a.forEach(c -> {
                coMap.put(c.getArticleId(), c);
            });
        });
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
    public ResponseMapper addArticle(TraceRequest request, String title, String content) {
        if (!request.isLogin()) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.LOGIN_INVALIDATE.statusCode())
                    .message("登录失效,请重新登录");
        }
        User user = request.getUser();
        Article t = new Article();
        ArticleAttr attr = new ArticleAttr();
        RedisLock lock = RedisLock.getRedisLock("addArticle:" + user.getUuid() + ":" + title);
        String uuid = lock.lock(() -> {
            try {
                // 这里睡眠一会,防止执行过快锁释放,
                // 而前端因网络延迟点击多次
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            t.setContent(content)
                    .setTitle(title)
                    .setUserId(user.getUuid())
                    .setUuid(IdGenerator.uuid());
            attr.setArticleId(t.getUuid())
                    .setUuid(IdGenerator.uuid());
            SpringBeanUtils.getBean(ArticleServiceImpl.class)
                    .doInsertArticle(t, attr, user.getUuid());
            return t.getUuid();
        });
        if (uuid == null) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.FAILED.statusCode())
                    .message("发表失败");
        }
        return ResponseMapper.createMapper().data(uuid);
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
//        try {
//            BaseProducer.produce(Type.QUEUE, MqContant.EMAIL, JSON.toJSONString(builder));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }

    @Override
    public ResponseMapper editArticle(TraceRequest request, String title, String content, String userId,
            String articleId) {
        if (!request.isLogin()) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.LOGIN_INVALIDATE.statusCode())
                    .message("登录失效,请重新登录");
        }
        return this.doEdit(userId, title, content, articleId);
    }

    private ResponseMapper doEdit(String userId, String title, String content, String articleId) {
        Article ar = articleDao.getByUuid(articleId);
        if (ar == null || !userId.equals(ar.getUserId())) {
            return ResponseMapper.createMapper().code(ResponseCode.NO_DATA.statusCode());
        }
        Article temp = new Article();
        temp.setTitle(title)
                .setContent(content)
                .setUuid(articleId);
        if (this.articleDao.update(temp) <= 0) {
            return ResponseMapper.createMapper().code(ResponseCode.FAILED.statusCode());
        }
        return ResponseMapper.createMapper().data(articleId);
    }

    @Override
    public ResponseMapper addReadNum(TraceRequest request, String articleId) {
        String ip = request.getHeader().getRemoteHost();
        if (JedisUtils.get("user:login:" + ip) != null) {
            return ResponseMapper.createMapper();
        }
        ArticleAttr temp = new ArticleAttr()
                .setReadNum(1)
                .setArticleId(articleId);
        this.articleAttrDao.updateByAddition(temp);
        JedisUtils.set("user:login:" + ip, temp.getReadNum().toString(), 600);
        return ResponseMapper.createMapper().data(temp.getReadNum());
    }

    @Override
    public ResponseMapper addLike(TraceRequest request, String articleId, Integer isLike) {
        // 没登录 或失效
        if (!request.isLogin()) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.LOGIN_INVALIDATE.statusCode());
        }
        if (this.articleDao.isExist(articleId) < 1) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.ARGS_ERROR.statusCode());
        }
        ArticleLike t = new ArticleLike()
                .setArticleId(articleId)
                .setUserId(request.getUser().getUuid());
        SpringBeanUtils.getBean(ArticleServiceImpl.class)
                .handleLikeOrCollectNum(t);
        return ResponseMapper.createMapper();

    }

    private boolean addArticleLikeNum(ArticleLike a) {
        boolean isSendMsg = false;
        ArticleLike like = this.likeDao.get(a);
        // 已经点过
        if (like != null) {
            a.setStatus(-like.getStatus());
            if (this.likeDao.update(a) > 0 && a.getStatus() > 0) {
                isSendMsg = true;
            }
        } else if (this.likeDao.insert(a.setStatus(1).setNum(1)) > 0) {
            isSendMsg = true;
        }

        // 点赞+1 取消-1
        this.articleAttrDao.updateByAddition(new ArticleAttr()
                .setArticleId(a.getArticleId())
                .setLikeNum(a.getStatus()));
        return isSendMsg;
    }

    /**
     * isCollect -1取消收藏 1收藏
     */
    @Override
    public ResponseMapper addCollect(TraceRequest request, String articleId, Integer isCollect) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        // 没登录 或失效
        if (!request.isLogin()) {
            return mapper.code(ResponseCode.LOGIN_INVALIDATE.statusCode());
        }
        if (this.articleDao.isExist(articleId) < 1) {
            return mapper.code(ResponseCode.ARGS_ERROR.statusCode());
        }
        ArticleCollect t = new ArticleCollect()
                .setUserId(request.getUser().getUuid())
                .setArticleId(articleId);
        SpringBeanUtils.getBean(ArticleServiceImpl.class)
                .handleLikeOrCollectNum(t);
        return mapper;

    }

    private boolean addCollectNum(ArticleCollect a) {
        ArticleCollect co = this.collectDao.get(a);
        boolean isSendMsg = false;
        if (co != null) {
            a.setStatus(-co.getStatus());
            if (this.collectDao.update(a) > 0 && a.getStatus() > 0) {
                isSendMsg = true;
            }
        } else {
            // 没收藏
            a.setUuid(IdGenerator.uuid());
            if (this.collectDao.insert(a) > 0) {
                // 消息推送
                isSendMsg = true;
            }
        }

        ArticleAttr temp = new ArticleAttr()
                .setArticleId(a.getArticleId())
                // 1收藏 -1取消收藏
                .setCollectNum(a.getStatus());
        this.articleAttrDao.updateByAddition(temp);

        this.userService.addNum(NumCountType.CollectNum.ordinal(), temp.getCollectNum(), a.getUserId());
        return isSendMsg;
    }

    @Override
    public ResponseMapper comment(TraceRequest request, String articleId, String content) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        if (!request.isLogin()) {
            return mapper.code(ResponseCode.LOGIN_INVALIDATE.statusCode());
        }
        if (StringUtil.isEmpty(content)) {
            return mapper.code(ResponseCode.ARGS_ERROR.statusCode());
        }
        User user = request.getUser();
        RedisLock lock = RedisLock.getRedisLock("comment:" + user.getUuid() + ":" + articleId);
        ResponseMapper ret = lock.lock(() -> {
            boolean isSendMsg = false;
            ArticleComment co = new ArticleComment();
            Article ar = this.articleDao.getByUuid(articleId);
            if (ar == null) {
                return mapper.code(ResponseCode.ARGS_ERROR.statusCode());
            }
            co.setArticleId(articleId)
                    .setReplyerId(user.getUuid())
                    .setContent(content)
                    .setAuthorId(ar.getUserId())
                    .setUuid(IdGenerator.uuid());
            // 为表情内容的设置
            this.arCommentDao.predo();
            if (this.arCommentDao.insert(co) > 0) {
                this.addCommentNum(articleId, 1);
                // 别人评论的
                if (!co.getAuthorId().equals(co.getReplyerId())) {
                    // 消息推送
                    isSendMsg = true;
                }
            }
            Map<String, String> map = new HashMap<>(8);
            map.put("authorId", ar.getUserId());
            map.put("replyerId", user.getUuid());
            map.put("replyerName", user.getNickname());
            map.put("replyerAvatar", user.getAvatar());
            map.put("content", content);
            map.put("createDate", TimeUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm"));

            if (isSendMsg) {
                try {
                    this.messageService.sendMsgEvent(new Message()
                            .setSenderId(user.getUuid())
                            .setReceiverId(ar.getUserId())
                            .setType(MsgType.NEWS.statusCode())
                            .setBizId(articleId)
                            .setBizType(BizType.ARTICLE.statusCode())
                            .setBizAction(BizAction.COMMENT.statusCode())
                            .setContent(content)
                            .setReply(null));
                } catch (Exception e) {
                    // do nothing
                }

            }
            return mapper.data(map);
        });
        if (ret == null) {
            return mapper.code(ResponseCode.FAILED.statusCode());
        }
        return ret;

    }

    @Override
    public ResponseMapper reply(TraceRequest request, String commentId, String replyContent) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        if (!request.isLogin()) {
            return mapper.code(ResponseCode.LOGIN_INVALIDATE.statusCode());
        }
        if (StringUtil.isEmpty(replyContent)) {
            return mapper.code(ResponseCode.ARGS_ERROR.statusCode());
        }
        User user = request.getUser();
        ArticleComment comment = this.arCommentDao.getByUuid(commentId);
        if (comment == null) {
            return mapper.code(ResponseCode.NO_DATA.statusCode());
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
        return mapper;
    }

    private void addCommentNum(String articleId, int num) {
        ArticleAttr temp = new ArticleAttr()
                .setArticleId(articleId)
                .setCommentNum(num);
        this.articleAttrDao.updateByAddition(temp);
    }

    @Override
    public ResponseMapper comments(TraceRequest request, String articleId) {
        int pageNum = request.getHeader().getPageNum();
        int pageSize = request.getHeader().getPageSize();
        pageSize = pageSize > 32 ? 32 : pageSize;

        Page<?> page = PageHelper.startPage(pageNum, pageSize);
        final List<ArticleCommentVo> list = this.arCommentDao.findList(articleId);
        if (list.isEmpty()) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.NO_DATA.statusCode());
        }
        List<Map<String, Object>> total = this.handleCommentsList(list, request.getUser(), request.isLogin());
        return ResponseMapper.createMapper()
                .count(page.getTotal())
                .data(total);
    }

    private List<Map<String, Object>> handleCommentsList(final List<ArticleCommentVo> list,
            final User user, final boolean isLogin) {
        int size = list.size();
        List<CommentLike> likeQueryList = new ArrayList<>(size);
        // 查询条件
        if (isLogin) {
            list.forEach(a -> {
                likeQueryList.add(new CommentLike()
                        .setUserId(user.getUuid())
                        .setCommentId(a.getUuid()));
            });
        }
        // 查询数据
        List<CommentLike> likeList = null;
        if (!likeQueryList.isEmpty()) {
            likeList = this.arCommentDao.getLikes(likeQueryList);
        }
        // 封装数据
        Map<String, CommentLike> likeMap = new HashMap<>(16);
        Optional.ofNullable(likeList).ifPresent(a -> {
            a.forEach(c -> {
                likeMap.put(c.getCommentId(), c);
            });
        });
        // 处理数据
        Map<String, Object> map = null;
        List<Map<String, Object>> total = new ArrayList<>(size);
        CommentLike c = null;
        for (ArticleCommentVo a : list) {
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
    public ResponseMapper newComments(TraceRequest request, String articleId) {
        List<ArticleCommentVo> list = this.arCommentDao.findNewComments(articleId);
        if (list == null || list.isEmpty()) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.NO_DATA.statusCode());
        }
        List<Map<String, Object>> total = this.handleCommentsList(list, request.getUser(), request.isLogin());
        return ResponseMapper.createMapper().data(total);
    }

    @Override
    public ResponseMapper userComments(TraceRequest request, String userId) {
        if (!request.isLogin()) {
            return ResponseMapper.createMapper().code(ResponseCode.LOGIN_INVALIDATE.statusCode());
        }
        int pageNum = request.getHeader().getPageNum();
        int pageSize = request.getHeader().getPageSize();
        pageSize = pageSize > 32 ? 32 : pageSize;

        Page<?> page = PageHelper.startPage(pageNum, pageSize);
        CommonQuery query = new CommonQuery().setReplyerId(userId);
        List<ArticleComment> list = this.arCommentDao.findByList(query);
        if (list.isEmpty()) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.NO_DATA.statusCode());
        }
        List<Map<String, Object>> total = this.handleUserCommentsList(list);
        return ResponseMapper.createMapper()
                .count(page.getTotal())
                .data(total);
    }

    private List<Map<String, Object>> handleUserCommentsList(final List<ArticleComment> list) {
        int size = list.size();
        // 处理数据
        Map<String, Object> map = null;
        List<Map<String, Object>> total = new ArrayList<>(size);
        List<String> articleIds = new ArrayList<>();
        List<String> parentCommentIds = new ArrayList<>();
        List<String> parentReplyerIds = new ArrayList<>();
        for (ArticleComment a : list) {
            articleIds.add(a.getArticleId());
            if (StringUtil.isNotEmpty(a.getParentId())) {
                parentCommentIds.add(a.getParentId());
                parentReplyerIds.add(a.getParentReplyerId());
            }
        }
        CommonQuery query = new CommonQuery();
        query.setArticleIds(articleIds);
        List<Article> articles = this.articleDao.findByList(query);
        Map<String, Article> arMap = new HashMap<>(articles.size());
        articles.forEach(a -> {
            arMap.put(a.getUuid(), a);
        });
        Map<String, UserVo> parentReplyerMap = new HashMap<>(parentReplyerIds.size());
        Map<String, ArticleComment> coMap = new HashMap<>(parentReplyerIds.size());
        if (!parentReplyerIds.isEmpty()) {
            List<UserVo> uservos = this.userService.findVoByUuid(parentReplyerIds);
            uservos.forEach(a -> {
                parentReplyerMap.put(a.getUserId(), a);
            });
            query = new CommonQuery();
            query.setCommentIds(parentCommentIds);
            List<ArticleComment> comments = this.arCommentDao.findByList(query);
            comments.forEach(a -> {
                coMap.put(a.getUuid(), a);
            });
        }

        for (ArticleComment a : list) {
            map = MapleUtil.wrap()
                    .stick("commentId", a.getUuid())
                    .stick("createDate", TimeUtils.format(a.getCreateDate(), "yyyy-MM-dd HH:mm"))
                    .stick("replyerName", "")
                    .stick("parentReplyerId", a.getParentReplyerId())
                    .stick("content", a.getContent())
                    .stick("articleTitle", arMap.get(a.getArticleId()).getTitle())
                    .map();
            if (!StringUtil.isEmpty(a.getParentId())) {
                ArticleComment co = coMap.get(a.getParentId());
                if (co != null) {
                    map.put("parentContent", co.getContent());
                } else {
                    map.put("parentContent", "已删除");
                }
                map.put("parentReplyerName", parentReplyerMap.get(a.getParentReplyerId()).getNickname());
            }
            total.add(map);
        }
        return total;
    }

    /**
     * =============================评论相关===========================
     */

    @Override
    public ResponseMapper addCommentLike(TraceRequest request, String commentId, Integer isLike) {
        // 没登录 或失效
        if (!request.isLogin()) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.LOGIN_INVALIDATE.statusCode());
        }
        CommentLike t = new CommentLike()
                .setUserId(request.getUser().getUuid())
                .setCommentId(commentId);
        SpringBeanUtils.getBean(ArticleServiceImpl.class)
                .handleLikeOrCollectNum(t);

        return ResponseMapper.createMapper();
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
            isSendMsg = this.addCommentLikeNum(c);

        } else if (t instanceof ArticleLike) {
            ArticleLike a = (ArticleLike) t;
            msg.setSenderId(a.getUserId())
                    .setType(MsgType.NEWS.statusCode())
                    .setBizId(a.getArticleId())
                    .setBizType(BizType.ARTICLE.statusCode())
                    .setBizAction(BizAction.ARTICLE_LIKE.statusCode());
            isSendMsg = this.addArticleLikeNum(a);

        } else if (t instanceof ArticleCollect) {
            ArticleCollect a = (ArticleCollect) t;
            msg.setSenderId(a.getUserId())
                    .setType(MsgType.NEWS.statusCode())
                    .setBizId(a.getArticleId())
                    .setBizType(BizType.ARTICLE.statusCode())
                    .setBizAction(BizAction.COLLECT.statusCode());
            isSendMsg = this.addCollectNum(a);

        }

        if (isSendMsg) {
            try {
                this.messageService.sendMsgEvent(msg);
            } catch (Exception e) {
                // do nothing
            }
        }
    }

    private boolean addCommentLikeNum(CommentLike c) {
        CommentLike like = this.arCommentDao.getLike(c);
        boolean isSendMsg = false;
        if (like != null) {
            c.setStatus(-like.getStatus());
            if (this.arCommentDao.updateLike(c) > 0 && c.getStatus() > 0) {
                isSendMsg = true;
            }
        } else {
            this.arCommentDao.insertLike(c);
            // 没点过赞
            isSendMsg = true;
        }

        ArticleComment temp = new ArticleComment();
        temp.setUuid(c.getCommentId());
        ArticleComment ac = this.arCommentDao.getByUuid(c.getCommentId());
        temp.setNum(ac.getNum() + c.getStatus());
        temp.setOld((long) ac.getNum());
        // 进行乐观更新,最大重试1000次.可能会造成数据库连接量变大.
        int retry = 0;
        while (retry < 1000 && this.arCommentDao.updateOptimistic(temp) <= 0) {
            ac = this.arCommentDao.getByUuid(temp.getUuid());
            temp.setNum(ac.getNum() + c.getStatus());
            temp.setOld((long) ac.getNum());
            retry++;
        }
        if (retry > 0) {
            LOG.info("乐观锁更新操作,重试了{}次", retry);
        }
        return isSendMsg;
    }

    @Override
    public ResponseMapper latestOfUsers(TraceRequest request, String[] userIds) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        List<ArticleVo> list = this.articleDao.findLatestOfUsers(userIds);
        Optional.ofNullable(list).ifPresent(a -> {
            mapper.data(a);
        });
        return mapper;
    }

    @Override
    @Deprecated
    public ResponseMapper search(TraceRequest request, String keyword) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        // final Map<String, Object> map = ElasticUtils.searchWithCount(new String[] {
        // "website" },
        // new String[] { "article" }, 0, 10, keyword, new String[] { "title", "content"
        // });
        // return mapper.data(map);
        return mapper;
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
        CommonQuery t = new CommonQuery();
        while (i <= pageNum) {
            PageHelper.startPage(i++, pageSize, true);
            list = this.articleDao.findByList(t);
            if (!list.isEmpty()) {
                jsonMap = new HashMap<>(128);
                for (Article a : list) {
                    jsonMap.put(a.getUuid(), JSON.toJSONString(a));
                    // ElasticUtils.upsertList("website", "article", jsonMap);
                }
            }
            LOG.info("同步成功");
            return;
        }
        LOG.info("同步失败");
    }

    /* ==========================分类栏目相关=============================== */

    @Override
    public ResponseMapper addColumn(TraceRequest request, String name) {
        if (!request.isLogin()) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.LOGIN_INVALIDATE.statusCode())
                    .message("登录失效,请重新登录");
        }
        User user = request.getUser();
        if (StringUtil.isBlank(name) || name.length() > 10) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.ARGS_ERROR.statusCode())
                    .message("请正确填写名称");
        }
        RedisLock lock = RedisLock.getRedisLock("addColumn:" + user.getUuid() + ":" + name);
        ResponseMapper ret = lock.lock(() -> {
            ArticleColumn column = new ArticleColumn();
            column.setUserId(user.getUuid());
            if (this.columnDao.count(column) > 50) {
                return ResponseMapper.createMapper()
                        .code(ResponseCode.FAILED.statusCode())
                        .message("我们觉得太多栏目可能并不那么美好");
            }
            column.setName(name)
                    .setUuid(IdGenerator.uuid());
            this.columnDao.insert(column);
            return ResponseMapper.createMapper().data(column);
        });
        if (ret == null) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.FAILED.statusCode())
                    .message("添加失败");
        }
        return ret;
    }

    @Override
    public ResponseMapper removeColumn(TraceRequest request, String columnId) {
        if (!request.isLogin()) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.LOGIN_INVALIDATE.statusCode())
                    .message("登录失效,请重新登录");
        }
        User user = request.getUser();
        ArticleColumn t = new ArticleColumn();
        t.setUserId(user.getUuid()).setUuid(columnId);
        this.columnDao.delete(t);
        return ResponseMapper.createMapper();
    }

    @Override
    public ResponseMapper updateColumn(TraceRequest request, String columnId, String name) {
        if (!request.isLogin()) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.LOGIN_INVALIDATE.statusCode())
                    .message("登录失效,请重新登录");
        }
        User user = request.getUser();
        ArticleColumn t = new ArticleColumn();
        t.setUserId(user.getUuid())
                .setName(name)
                .setUuid(columnId);
        this.columnDao.update(t);
        return ResponseMapper.createMapper();
    }

    @Override
    public ResponseMapper putOrTakeColumn(TraceRequest request, String columnId, String articleId) {
        if (!request.isLogin()) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.LOGIN_INVALIDATE.statusCode())
                    .message("登录失效,请重新登录");
        }

        User user = request.getUser();
        Article t = new Article();
        t.setUserId(user.getUuid())
                .setUuid(articleId);
        Article article = this.articleDao.getByUuid(columnId);
        ResponseMapper mapper = ResponseMapper.createMapper();
        if (article == null) {
            return mapper.code(ResponseCode.NO_DATA.statusCode());
        }
        if (!article.getUserId().equals(user.getUuid())) {
            return mapper.code(ResponseCode.NO_DATA.statusCode());
        }
        if (this.columnDao.isExist(columnId) <= 0) {
            return mapper.code(ResponseCode.NO_DATA.statusCode());
        }
        if ("".equals((columnId)) && StringUtil.isBlank(article.getColumnId())) {
            return mapper;
        }
        t.setColumnId(columnId);
        this.articleDao.update(t);
        return mapper;
    }

    @Override
    public ResponseMapper columns(TraceRequest request, String userId) {
        User user = request.getUser();
        CommonQuery query = new CommonQuery().setUserId(userId);
        if (user == null || !user.getUuid().equals(userId)) {
            query.setIsOpen(Flag.True.ordinal());
        }
        List<ArticleColumn> columnList = this.columnDao.findByList(query);
        List<Map<String, Object>> total = this.handleColumnList(columnList);
        return ResponseMapper.createMapper().data(total);
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
    public ResponseMapper findListByColumn(TraceRequest request, String userId, String columnId) {
        // 是否登录
        User user = request.getUser();
        boolean isLogin = request.isLogin();
        ArticleColumn co = new ArticleColumn().setUserId(userId);
        co.setUuid(columnId);
        int pageNum = request.getHeader().getPageNum();
        int pageSize = request.getHeader().getPageSize();
        pageSize = pageSize > 32 ? 32 : pageSize;

        List<ArticleVo> list = null;
        if (user == null) {
            // 没有登录
            co.setIsOpen(Flag.True.ordinal());
        }
        // 登录,但看的不是自己的
        else if (!user.getUuid().equals(userId)) {
            co.setIsOpen(Flag.True.ordinal());
        }

        PageHelper.startPage(pageNum, pageSize);
        list = this.articleDao.findByColumn(co);
        if (list.isEmpty()) {
            return ResponseMapper.createMapper();
        }

        List<Map<String, Object>> total = this.handleArticleVoList(list, request.getUser(), isLogin);
        return ResponseMapper.createMapper().data(total);
    }

    @Override
    public Article getByUuid(String uuid) {
        return this.articleDao.getByUuid(uuid);
    }

}