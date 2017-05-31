/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.article.entity;

import com.xiaoyu.common.base.BaseEntity;

public class ArticleLike extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private String articleId;
	private String userId;
	private Integer num;// 点了几次
	private Integer status;

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getArticleId() {
		return articleId;
	}

	public ArticleLike setArticleId(String articleId) {
		this.articleId = articleId;
		return this;
	}

	public String getUserId() {
		return userId;
	}

	public ArticleLike setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	public Integer getNum() {
		return num;
	}

	public ArticleLike setNum(Integer num) {
		this.num = num;
		return this;
	}

}
