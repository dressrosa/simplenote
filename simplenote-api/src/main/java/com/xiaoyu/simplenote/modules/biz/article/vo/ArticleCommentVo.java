/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.simplenote.modules.biz.article.vo;

import com.xiaoyu.simplenote.common.base.BaseEntity;

/**
 * @author xiaoyu 2016年3月29日
 */
public class ArticleCommentVo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String articleId;
    private String replyerName;
    private String replyerAvatar;
    private String authorId;
    private String parentId;
    private String content;
    private String replyerId;
    private String parentReplyerName;
    private String parentReplyerId;
    private Integer num;

    public Integer getNum() {
        return this.num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getReplyerName() {
        return this.replyerName;
    }

    public void setReplyerName(String replyerName) {
        this.replyerName = replyerName;
    }

    public String getReplyerAvatar() {
        return this.replyerAvatar;
    }

    public void setReplyerAvatar(String replyerAvatar) {
        this.replyerAvatar = replyerAvatar;
    }

    public String getParentReplyerName() {
        return this.parentReplyerName;
    }

    public void setParentReplyerName(String parentReplyerName) {
        this.parentReplyerName = parentReplyerName;
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

    public ArticleCommentVo setArticleId(String articleId) {
        this.articleId = articleId;
        return this;
    }

    public String getAuthorId() {
        return this.authorId;
    }

    public ArticleCommentVo setAuthorId(String authorId) {
        this.authorId = authorId;
        return this;
    }

    public String getParentId() {
        return this.parentId;
    }

    public ArticleCommentVo setParentId(String parentId) {
        this.parentId = parentId;
        return this;
    }

    public String getContent() {
        return this.content;
    }

    public ArticleCommentVo setContent(String content) {
        this.content = content;
        return this;
    }

    public String getReplyerId() {
        return this.replyerId;
    }

    public ArticleCommentVo setReplyerId(String replyerId) {
        this.replyerId = replyerId;
        return this;
    }

}