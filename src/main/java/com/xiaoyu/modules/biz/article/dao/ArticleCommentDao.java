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

	ArticleComment getForUpdate(@Param("id") String id);

	CommentLike getLikeForUpdate(CommentLike t);

	int updateLike(CommentLike t);

	int insertLike(CommentLike t);

	int isLiked(CommentLike t);

	CommentLike getLike(CommentLike t);

}
