/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.simplenote.modules.biz.article.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.github.pagehelper.Page;
import com.xiaoyu.simplenote.common.base.BaseDao;
import com.xiaoyu.simplenote.modules.biz.article.entity.Article;
import com.xiaoyu.simplenote.modules.biz.article.entity.ArticleColumn;
import com.xiaoyu.simplenote.modules.biz.article.vo.ArticleContentVo;
import com.xiaoyu.simplenote.modules.biz.article.vo.ArticleVo;

/**
 * @author xiaoyu 2016年3月29日
 */
@Repository
public interface ArticleDao extends BaseDao<Article> {

    List<ArticleVo> findByListWithAttr(@Param("userId") String userId);

    Article getForUpdate(@Param("uuid") String uuid);

    List<ArticleVo> findHotList();

    ArticleVo getVoByUuid(@Param("uuid") String uuid);

    Page<ArticleVo> findCollectList(@Param("userId") String userId);

    List<ArticleVo> findLatestOfUsers(String[] userIds);

    Integer count();

    List<ArticleVo> findByColumn(ArticleColumn co);

    ArticleContentVo getContentByArticleId(String articleId);

    List<ArticleContentVo> findContentByArticleIds(List<String> articleIds);

    int insertContent(ArticleContentVo vo);

    int updateContent(ArticleContentVo vo);
}
