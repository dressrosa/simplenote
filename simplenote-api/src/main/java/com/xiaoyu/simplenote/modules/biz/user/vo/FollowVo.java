/**
 * 唯有看书,不庸不扰
 */
package com.xiaoyu.simplenote.modules.biz.user.vo;

/**
 * @author:xiaoyu 2017年6月27日下午11:20:28
 * @description:关注
 */
public class FollowVo {

    private String userId;
    private String followerId;
    private String userName;
    private String followerName;
    private String userAvatar;
    private String userBackground;
    private String followerAvatar;
    private String followerBackground;

    public String getUserBackground() {
        return userBackground;
    }

    public void setUserBackground(String userBackground) {
        this.userBackground = userBackground;
    }

    public String getFollowerBackground() {
        return followerBackground;
    }

    public void setFollowerBackground(String followerBackground) {
        this.followerBackground = followerBackground;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFollowerId() {
        return this.followerId;
    }

    public void setFollowerId(String followerId) {
        this.followerId = followerId;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFollowerName() {
        return this.followerName;
    }

    public void setFollowerName(String followerName) {
        this.followerName = followerName;
    }

    public String getUserAvatar() {
        return this.userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getFollowerAvatar() {
        return this.followerAvatar;
    }

    public void setFollowerAvatar(String followerAvatar) {
        this.followerAvatar = followerAvatar;
    }

}
