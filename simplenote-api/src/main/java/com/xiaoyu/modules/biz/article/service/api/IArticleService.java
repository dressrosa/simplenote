/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.article.service.api;

import com.xiaoyu.common.base.ResponseMapper;
import com.xiaoyu.common.request.TraceRequest;
import com.xiaoyu.modules.biz.article.entity.Article;

/**
 * 2017年5月22日下午5:20:01
 * 
 * @author xiaoyu
 * @description
 */
public interface IArticleService {

    /**
     * 文章详情
     * 
     * @param articleId
     * @return
     */
    public ResponseMapper detail(String articleId);

    /**
     * 热门列表
     * 
     * @param request
     * @return
     */
    public ResponseMapper hotList(TraceRequest request);

    /**
     * 查询列表
     * 
     * @param request
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ResponseMapper list(TraceRequest request, String userId);

    /**
     * 收藏列表
     * 
     * @param request
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ResponseMapper collectList(TraceRequest request, String userId);

    /**
     * 增加文章
     * 
     * @param request
     * @param title
     * @param content
     * @return
     */
    public ResponseMapper addArticle(TraceRequest request, String title, String content);

    /**
     * 编辑文章
     * 
     * @param request
     * @param title
     * @param content
     * @param userId
     * @return
     */
    public ResponseMapper editArticle(TraceRequest request, String title, String content, String userId,
            String articleId);

    /**
     * 增加阅读数
     * 
     * @param request
     * @param articleId
     * @return
     */
    public ResponseMapper addReadNum(TraceRequest request, String articleId);

    /**
     * 点赞
     * 
     * @param request
     * @param articleId
     * @param isLike
     * @return
     */
    public ResponseMapper addLike(TraceRequest request, String articleId, Integer isLike);

    /**
     * 评论点赞
     * 
     * @param request
     * @param commentId
     * @param isLike
     * @return
     */
    public ResponseMapper addCommentLike(TraceRequest request, String commentId, Integer isLike);

    /**
     * 收藏
     * 
     * @param request
     * @param articleId
     * @param isCollect
     * @return
     */
    public ResponseMapper addCollect(TraceRequest request, String articleId, Integer isCollect);

    /**
     * 评论
     * 
     * @param request
     * @param articleId
     * @param content
     * @return
     */
    public ResponseMapper comment(TraceRequest request, String articleId, String content);

    /**
     * 回复
     * 
     * @param request
     * @param commentId
     * @param replyContent
     * @return
     */
    public ResponseMapper reply(TraceRequest request, String commentId, String replyContent);

    /**
     * 评论列表
     * 
     * @param request
     * @param articleId
     * @return
     */
    public ResponseMapper comments(TraceRequest request, String articleId);

    /**
     * 最新评论
     * 
     * @param request
     * @param articleId
     * @return
     */
    public ResponseMapper newComments(TraceRequest request, String articleId);

    /**
     * 最新文章
     * 
     * @param request
     * @param userIds
     * @return
     */
    public ResponseMapper latestOfUsers(TraceRequest request, String[] userIds);

    /**
     * 搜索
     * 
     * @param request
     * @param keyword
     * @return
     */
    public ResponseMapper search(TraceRequest request, String keyword);

    /**
     * 同步到es
     * 
     * @return
     */
    public void synElastic();

    /**
     * 增加栏目
     * 
     * @param request
     * @param name
     * @return
     */
    public ResponseMapper addColumn(TraceRequest request, String name);

    /**
     * 删除栏目,不会删除文章
     * 
     * @param request
     * @param columnId
     * @return
     */
    public ResponseMapper removeColumn(TraceRequest request, String columnId);

    /**
     * 更新栏目名称
     * 
     * @param request
     * @param columnId
     * @param name
     * @return
     */
    public ResponseMapper updateColumn(TraceRequest request, String columnId, String name);

    /**
     * 把文章放入/移出栏目
     * 
     * @param request
     * @param columnId
     * @param articleId
     * @return
     */
    public ResponseMapper putOrTakeColumn(TraceRequest request, String columnId, String articleId);

    /**
     * 栏目列表
     * 
     * @param request
     * @param userId
     * @return
     */
    public ResponseMapper columns(TraceRequest request, String userId);

    /**
     * 获取栏目里面的文章
     * 
     * @param request
     * @param userId
     * @param columnId
     * @return
     */
    public ResponseMapper findListByColumn(TraceRequest request, String userId, String columnId);

    public Article getByUuid(String uuid);

    public ResponseMapper userComments(TraceRequest req, String userId);

}
