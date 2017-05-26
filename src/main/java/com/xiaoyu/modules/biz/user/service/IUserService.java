/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.user.service;

import javax.servlet.http.HttpServletRequest;

public interface IUserService {

	public String login(HttpServletRequest request, String loginName, String password);

	public String loginRecord(HttpServletRequest request, String userId, String device);
	
	public String userDetail(HttpServletRequest request, String userId);
}
