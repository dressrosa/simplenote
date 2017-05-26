/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.article.entity;

import com.xiaoyu.common.base.BaseEntity;

/**
 * @author xiaoyu 2016年3月29日
 */
public class Article extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String userId;
	private String title;
	private String content;

	private Integer readNum;
	private Integer commentNum;

	public Integer getReadNum() {
		return readNum;
	}

	public Article setReadNum(Integer readNum) {
		this.readNum = readNum;
		return this;
	}

	public Integer getCommentNum() {
		return commentNum;
	}

	public Article setCommentNum(Integer commentNum) {
		this.commentNum = commentNum;
		return this;
	}

	public String getUserId() {
		return userId;
	}

	public Article setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public Article setTitle(String title) {
		this.title = title;
		return this;
	}

	public String getContent() {
		return content;
	}

	public Article setContent(String content) {
		this.content = content;
		return this;
	}

}
