/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.user.entity;

import com.xiaoyu.common.base.BaseEntity;

/**
 * @author xiaoyu 2016年4月8日
 */
public class UserAttr extends BaseEntity {

	private static final long serialVersionUID = 1L;
	private String userId;
	private Integer articleNum;// 发表文章数
	private Integer commentNum;// 评论文章数
	private Integer collectNum;// 收藏文章数
	private Integer viewedNum;// 主页浏览数
	private Integer followerNum;// 追随者数

	public Integer getFollowerNum() {
		return followerNum;
	}

	public void setFollowerNum(Integer followerNum) {
		this.followerNum = followerNum;
	}

	public String getUserId() {
		return userId;
	}

	public UserAttr setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	public Integer getArticleNum() {
		return articleNum;
	}

	public UserAttr setArticleNum(Integer articleNum) {
		this.articleNum = articleNum;
		return this;
	}

	public Integer getCommentNum() {
		return commentNum;
	}

	public UserAttr setCommentNum(Integer commentNum) {
		this.commentNum = commentNum;
		return this;
	}

	public Integer getViewedNum() {
		return viewedNum;
	}

	public UserAttr setViewedNum(Integer viewedNum) {
		this.viewedNum = viewedNum;
		return this;
	}

	public Integer getCollectNum() {
		return collectNum;
	}

	public UserAttr setCollectNum(Integer collectNum) {
		this.collectNum = collectNum;
		return this;
	}

}