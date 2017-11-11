/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.article.entity;

import com.xiaoyu.common.base.BaseEntity;

/**
 * @author xiaoyu 2016年3月29日
 */
public class ArticleComment extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String articleId;
    private String authorId;
    private String parentId;
    private String parentReplyerId;
    private String content;
    private String replyerId;
    private Integer num;// 被赞数

    public Integer getNum() {
        return this.num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getParentReplyerId() {
        return this.parentReplyerId;
    }

    public void setParentReplyerId(String parentReplyerId) {
        this.parentReplyerId = parentReplyerId;
    }

    public String getArticleId() {
        return this.articleId;
    }

    public ArticleComment setArticleId(String articleId) {
        this.articleId = articleId;
        return this;
    }

    public String getAuthorId() {
        return this.authorId;
    }

    public ArticleComment setAuthorId(String authorId) {
        this.authorId = authorId;
        return this;
    }

    public String getParentId() {
        return this.parentId;
    }

    public ArticleComment setParentId(String parentId) {
        this.parentId = parentId;
        return this;
    }

    public String getContent() {
        return this.content;
    }

    public ArticleComment setContent(String content) {
        this.content = content;
        return this;
    }

    public String getReplyerId() {
        return this.replyerId;
    }

    public ArticleComment setReplyerId(String replyerId) {
        this.replyerId = replyerId;
        return this;
    }

}