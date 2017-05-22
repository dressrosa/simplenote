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

	public void setReadNum(Integer readNum) {
		this.readNum = readNum;
	}

	public Integer getCommentNum() {
		return commentNum;
	}

	public void setCommentNum(Integer commentNum) {
		this.commentNum = commentNum;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
