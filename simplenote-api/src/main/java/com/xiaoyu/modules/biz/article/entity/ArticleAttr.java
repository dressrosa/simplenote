/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.article.entity;

import com.xiaoyu.common.base.BaseEntity;

/**
 * @author xiaoyu 2016年4月8日
 */
public class ArticleAttr extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String articleId;
    private Integer readNum;
    private Integer commentNum;
    private Integer collectNum;
    private Integer likeNum;

    public String getArticleId() {
        return this.articleId;
    }

    public ArticleAttr setArticleId(String articleId) {
        this.articleId = articleId;
        return this;
    }

    public Integer getReadNum() {
        return this.readNum;
    }

    public ArticleAttr setReadNum(Integer readNum) {
        this.readNum = readNum;
        return this;
    }

    public Integer getCommentNum() {
        return this.commentNum;
    }

    public ArticleAttr setCommentNum(Integer commentNum) {
        this.commentNum = commentNum;
        return this;
    }

    public Integer getCollectNum() {
        return this.collectNum;
    }

    public ArticleAttr setCollectNum(Integer collectNum) {
        this.collectNum = collectNum;
        return this;
    }

    public Integer getLikeNum() {
        return this.likeNum;
    }

    public ArticleAttr setLikeNum(Integer likeNum) {
        this.likeNum = likeNum;
        return this;
    }

}
