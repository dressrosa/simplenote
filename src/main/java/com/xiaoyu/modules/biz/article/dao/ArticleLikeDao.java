/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.article.dao;

import org.springframework.stereotype.Repository;

import com.xiaoyu.common.base.BaseDao;
import com.xiaoyu.modules.biz.article.entity.ArticleLike;

/**
 * @author xiaoyu 2016年3月29日
 */
@Repository
public interface ArticleLikeDao extends BaseDao<ArticleLike> {

	ArticleLike  getForUpdate(ArticleLike t);

}
