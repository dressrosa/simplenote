/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.article.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.github.pagehelper.Page;
import com.xiaoyu.common.base.BaseDao;
import com.xiaoyu.modules.biz.article.entity.Article;
import com.xiaoyu.modules.biz.article.vo.ArticleVo;

/**
 * @author xiaoyu 2016年3月29日
 */
@Repository
public interface ArticleDao extends BaseDao<Article> {

	List<ArticleVo> findByListWithAttr(@Param("userId") String userId);

	Article getForUpdate(@Param("id") String articleId);

	List<ArticleVo> findHotList();

	ArticleVo getVo(@Param("id") String articleId);

	Page<ArticleVo> findCollectList(@Param("userId") String userId);

	List<ArticleVo> findLatestOfUsers(String[] userIds);

	Integer count();

}
