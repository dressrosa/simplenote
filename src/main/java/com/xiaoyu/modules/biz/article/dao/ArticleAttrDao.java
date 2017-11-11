/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.article.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.xiaoyu.common.base.BaseDao;
import com.xiaoyu.modules.biz.article.entity.ArticleAttr;

/**
 * @author xiaoyu 2016年4月8日
 */
@Repository
public interface ArticleAttrDao extends BaseDao<ArticleAttr> {

    ArticleAttr getForUpdate(@Param("articleId") String articleId);

}
