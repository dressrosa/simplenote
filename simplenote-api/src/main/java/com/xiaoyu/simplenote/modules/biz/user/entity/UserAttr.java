/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.simplenote.modules.biz.user.entity;

import com.xiaoyu.simplenote.common.base.BaseEntity;

/**
 * @author xiaoyu 2016年4月8日
 */
public class UserAttr extends BaseEntity {

    private static final long serialVersionUID = 1L;
    private String userId;
    // 发表文章数
    private Integer articleNum;
    // 评论文章数
    private Integer commentNum;
    // 收藏文章数
    private Integer collectNum;
    // 主页浏览数
    private Integer viewedNum;
    // 追随者数
    private Integer followerNum;

    public Integer getFollowerNum() {
        return this.followerNum;
    }

    public void setFollowerNum(Integer followerNum) {
        this.followerNum = followerNum;
    }

    public String getUserId() {
        return this.userId;
    }

    public UserAttr setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public Integer getArticleNum() {
        return this.articleNum;
    }

    public UserAttr setArticleNum(Integer articleNum) {
        this.articleNum = articleNum;
        return this;
    }

    public Integer getCommentNum() {
        return this.commentNum;
    }

    public UserAttr setCommentNum(Integer commentNum) {
        this.commentNum = commentNum;
        return this;
    }

    public Integer getViewedNum() {
        return this.viewedNum;
    }

    public UserAttr setViewedNum(Integer viewedNum) {
        this.viewedNum = viewedNum;
        return this;
    }

    public Integer getCollectNum() {
        return this.collectNum;
    }

    public UserAttr setCollectNum(Integer collectNum) {
        this.collectNum = collectNum;
        return this;
    }

}