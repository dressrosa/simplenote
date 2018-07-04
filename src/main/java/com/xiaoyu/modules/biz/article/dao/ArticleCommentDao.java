/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.article.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.xiaoyu.common.base.BaseDao;
import com.xiaoyu.modules.biz.article.entity.ArticleComment;
import com.xiaoyu.modules.biz.article.entity.CommentLike;
import com.xiaoyu.modules.biz.article.vo.ArticleCommentVo;

/**
 * @author xiaoyu 2016年3月29日
 */
@Repository
public interface ArticleCommentDao extends BaseDao<ArticleComment> {

    List<ArticleCommentVo> findNewComments(@Param("articleId") String articleId);

    List<ArticleCommentVo> findList(String articleId);

    /**
     * 行级锁
     * 
     * @param uuid
     * @return
     */
    ArticleComment getForUpdate(@Param("uuid") String uuid);

    CommentLike getLikeForUpdate(CommentLike t);

    int updateLike(CommentLike t);

    int insertLike(CommentLike t);

    int isLiked(CommentLike t);

    int predo();

    CommentLike getLike(CommentLike t);

    List<CommentLike> getLikes(List<CommentLike> list);

    /**
     * 乐观更新
     * 
     * @param c
     * @return
     */
    int updateOptimistic(ArticleComment c);
}
