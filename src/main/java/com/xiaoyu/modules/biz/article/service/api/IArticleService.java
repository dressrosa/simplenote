/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.article.service.api;

import javax.servlet.http.HttpServletRequest;

/**
 * 2017年5月22日下午5:20:01
 * 
 * @author xiaoyu
 * @description
 */
public interface IArticleService {

	public String detail(String articleId);

	public String hotList(HttpServletRequest request);

	public String list(HttpServletRequest request, String userId, Integer pageNum, Integer pageSize);

	public String addArticle(HttpServletRequest request, String title, String content, String userId, String token);

	public String addReadNum(HttpServletRequest request, String articleId);

	public String addLike(HttpServletRequest request, String articleId, Integer isLike);

	public String addCollect(HttpServletRequest request, String articleId, Integer isCollect);

	public String comment(HttpServletRequest request,String articleId, String content);
}
