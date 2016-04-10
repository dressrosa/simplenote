/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */ 
package com.xiaoyu.modules.biz.article.entity;

import com.xiaoyu.common.base.BaseEntity;

/**
 * @author xiaoyu
 *2016年4月8日
 */
public class ArticleAttr extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String articleId;
	private Integer readNum;
	private Integer commentNum;
	public String getArticleId() {
		return articleId;
	}
	public void setArticleId(String articleId) {
		this.articleId = articleId;
	}
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
	
	
}
