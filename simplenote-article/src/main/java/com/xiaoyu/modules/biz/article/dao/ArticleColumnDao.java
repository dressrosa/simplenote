/**
 *  唯有读书,不慵不扰
 */
package com.xiaoyu.modules.biz.article.dao;

import org.springframework.stereotype.Repository;

import com.xiaoyu.common.base.BaseDao;
import com.xiaoyu.modules.biz.article.entity.ArticleColumn;

@Repository
public interface ArticleColumnDao extends BaseDao<ArticleColumn> {

    public int count(ArticleColumn column);
}
