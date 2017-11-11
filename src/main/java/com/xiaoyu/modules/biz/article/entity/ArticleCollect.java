/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.article.entity;

import com.xiaoyu.common.base.BaseEntity;

public class ArticleCollect extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String articleId;
    private String userId;
    private Integer status;

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getArticleId() {
        return this.articleId;
    }

    public ArticleCollect setArticleId(String articleId) {
        this.articleId = articleId;
        return this;
    }

    public String getUserId() {
        return this.userId;
    }

    public ArticleCollect setUserId(String userId) {
        this.userId = userId;
        return this;
    }

}
