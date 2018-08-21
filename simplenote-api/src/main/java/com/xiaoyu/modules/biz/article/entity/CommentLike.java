/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.article.entity;

import com.xiaoyu.common.base.BaseEntity;

/**
 * 2017年6月13日下午5:31:43
 * 
 * @author xiaoyu
 * @description 评论点赞
 */
public class CommentLike extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String commentId;
    private String userId;

    private Integer status;

    public String getCommentId() {
        return this.commentId;
    }

    public CommentLike setCommentId(String commentId) {
        this.commentId = commentId;
        return this;
    }

    public Integer getStatus() {
        return this.status;
    }

    public CommentLike setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public String getUserId() {
        return this.userId;
    }

    public CommentLike setUserId(String userId) {
        this.userId = userId;
        return this;
    }

}
