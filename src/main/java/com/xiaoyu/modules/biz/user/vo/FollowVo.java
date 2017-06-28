/**
 * 唯有看书,不庸不扰
 */
package com.xiaoyu.modules.biz.user.vo;

/**
 * @author:xiaoyu 2017年6月27日下午11:20:28
 *
 * @description:关注
 */
public class FollowVo {

	private String userId;
	private String followerId;
	private String userName;
	private String followerName;
	private String userAvatar;
	private String followerAvatar;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFollowerId() {
		return followerId;
	}

	public void setFollowerId(String followerId) {
		this.followerId = followerId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFollowerName() {
		return followerName;
	}

	public void setFollowerName(String followerName) {
		this.followerName = followerName;
	}

	public String getUserAvatar() {
		return userAvatar;
	}

	public void setUserAvatar(String userAvatar) {
		this.userAvatar = userAvatar;
	}

	public String getFollowerAvatar() {
		return followerAvatar;
	}

	public void setFollowerAvatar(String followerAvatar) {
		this.followerAvatar = followerAvatar;
	}

}
