/**
 * 唯有看书,不庸不扰
 */
package com.xiaoyu.modules.biz.user.entity;

import com.xiaoyu.common.base.BaseEntity;

/**
 * @author:xiaoyu 2017年6月27日下午11:20:28
 *
 * @description:关注
 */
public class Follow extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String userId;
    private String followerId;

    public String getUserId() {
        return this.userId;
    }

    public Follow setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getFollowerId() {
        return this.followerId;
    }

    public Follow setFollowerId(String followerId) {
        this.followerId = followerId;
        return this;
    }

}
