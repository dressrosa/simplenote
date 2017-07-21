/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.user.service.api;

import javax.servlet.http.HttpServletRequest;

public interface IUserService {

	public String login(HttpServletRequest request, String loginName, String password);

	public String loginRecord(HttpServletRequest request, String userId, String device);

	public String userDetail(HttpServletRequest request, String userId);

	public String register(HttpServletRequest request, String loginName, String password);

	public String editUser(HttpServletRequest request, String userId, String content, Integer flag);

	/* 关注相关 */
	public String followUser(HttpServletRequest request, String userId, String followTo);

	public String cancelFollow(HttpServletRequest request, String userId, String followTo);

	public String isFollowed(HttpServletRequest request, String userId, String followTo);

	public String follower(HttpServletRequest request, String userId);
	

	public String following(HttpServletRequest request, String userId);

	public String commonNums(HttpServletRequest request, String userId);
}
