/**
 * 
 */
package com.xiaoyu.simplenote.common.base;

import java.util.Collection;

/**
 * 通用查询类
 * 
 * @author hongyu
 * @date 2019-01
 * @description
 */
public class CommonQuery {

    private String userId;
    private String articleId;
    private Integer id;

    private Integer isOpen;
    private String replyerId;
    private Collection<Integer> ids;

    private Collection<String> articleIds;
    private Collection<String> userIds;
    private Collection<String> commentIds;
    private Collection<String> bizIds;

    private String bizId;

    public Collection<String> getBizIds() {
        return bizIds;
    }

    public CommonQuery setBizIds(Collection<String> bizIds) {
        this.bizIds = bizIds;
        return this;
    }

    public String getBizId() {
        return bizId;
    }

    public CommonQuery setBizId(String bizId) {
        this.bizId = bizId;
        return this;
    }

    public Collection<String> getArticleIds() {
        return articleIds;
    }

    public CommonQuery setArticleIds(Collection<String> articleIds) {
        this.articleIds = articleIds;
        return this;
    }

    public Integer getIsOpen() {
        return isOpen;
    }

    public CommonQuery setIsOpen(Integer isOpen) {
        this.isOpen = isOpen;
        return this;
    }

    public String getReplyerId() {
        return replyerId;
    }

    public CommonQuery setReplyerId(String replyerId) {
        this.replyerId = replyerId;
        return this;
    }

    public Collection<String> getCommentIds() {
        return commentIds;
    }

    public CommonQuery setCommentIds(Collection<String> commentIds) {
        this.commentIds = commentIds;
        return this;
    }

    public Collection<String> getUserIds() {
        return userIds;
    }

    public CommonQuery setUserIds(Collection<String> userIds) {
        this.userIds = userIds;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public CommonQuery setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getArticleId() {
        return articleId;
    }

    public CommonQuery setArticleId(String articleId) {
        this.articleId = articleId;
        return this;
    }

    public Integer getId() {
        return id;
    }

    public CommonQuery setId(Integer id) {
        this.id = id;
        return this;
    }

    public Collection<Integer> getIds() {
        return ids;
    }

    public CommonQuery setIds(Collection<Integer> ids) {
        this.ids = ids;
        return this;
    }

}
