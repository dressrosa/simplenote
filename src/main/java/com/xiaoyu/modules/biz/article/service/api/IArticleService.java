/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.article.service.api;

/**
 * 2017年5月22日下午5:20:01
 * 
 * @author xiaoyu
 * @description
 */
public interface IArticleService {

	public String detail(String articleId);

	public String publish(String userId, String content);
	
	public String hotList();
}
