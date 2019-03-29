/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.simplenote.modules.biz.article.entity;

import com.xiaoyu.simplenote.common.base.BaseEntity;

/**
 * @author hongyu
 * @date 2018-02
 * @description
 */
public class ArticleLike extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String articleId;
    private String userId;
    // 点了几次
    private Integer num;
    // -1 未点赞 1点赞
    private Integer status;

    public Integer getStatus() {
        return this.status;
    }

    public ArticleLike setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public String getArticleId() {
        return this.articleId;
    }

    public ArticleLike setArticleId(String articleId) {
        this.articleId = articleId;
        return this;
    }

    public String getUserId() {
        return this.userId;
    }

    public ArticleLike setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public Integer getNum() {
        return this.num;
    }

    public ArticleLike setNum(Integer num) {
        this.num = num;
        return this;
    }

}
