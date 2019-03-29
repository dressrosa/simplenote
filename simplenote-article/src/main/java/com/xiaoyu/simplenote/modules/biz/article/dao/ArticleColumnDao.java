/**
 *  唯有读书,不慵不扰
 */
package com.xiaoyu.simplenote.modules.biz.article.dao;

import org.springframework.stereotype.Repository;

import com.xiaoyu.simplenote.common.base.BaseDao;
import com.xiaoyu.simplenote.modules.biz.article.entity.ArticleColumn;

@Repository
public interface ArticleColumnDao extends BaseDao<ArticleColumn> {

    public int count(ArticleColumn column);
}
