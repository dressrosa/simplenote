/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.simplenote.modules.biz.article.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.xiaoyu.simplenote.common.base.BaseDao;
import com.xiaoyu.simplenote.modules.biz.article.entity.ArticleAttr;

/**
 * @author xiaoyu 2016年4月8日
 */
@Repository
public interface ArticleAttrDao extends BaseDao<ArticleAttr> {

    ArticleAttr getByArticleId(@Param("articleId") String articleId);

    /**
     * 悲观锁更新
     * 
     * @param articleId
     * @return
     */
    ArticleAttr getForUpdate(@Param("articleId") String articleId);

    /**
     * 乐观锁更新
     * 
     * @param attr
     * @return
     */
    int updateOptimistic(ArticleAttr attr);

    /**
     * 加法更新
     * 
     * @param attr
     * @return
     */
    int updateByAddition(ArticleAttr attr);
}
