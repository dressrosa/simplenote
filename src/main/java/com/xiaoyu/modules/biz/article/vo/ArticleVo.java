/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.article.vo;

import com.xiaoyu.common.base.BaseEntity;

/**
 * @author xiaoyu 2016年3月29日
 */
public class ArticleVo extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private String userId;
	private String title;
	private String content;
	private ArticleAttrVo attr;

	public ArticleAttrVo getAttr() {
		return attr;
	}

	public ArticleVo setAttr(ArticleAttrVo attr) {
		this.attr = attr;
		return this;
	}

	public String getUserId() {
		return userId;
	}

	public ArticleVo setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public ArticleVo setTitle(String title) {
		this.title = title;
		return this;
	}

	public String getContent() {
		return content;
	}

	public ArticleVo setContent(String content) {
		this.content = content;
		return this;
	}

}
