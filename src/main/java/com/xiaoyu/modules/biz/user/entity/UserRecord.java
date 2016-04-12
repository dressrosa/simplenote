/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */ 
package com.xiaoyu.modules.biz.user.entity;

import com.xiaoyu.common.base.BaseEntity;

/**
 * @author xiaoyu
 *2016年4月12日
 */
public class UserRecord extends BaseEntity{

	
	private static final long serialVersionUID = 1L;

	private String userId;
	private String loginIp;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getLoginIp() {
		return loginIp;
	}
	public void setLoginIp(String loginIp) {
		this.loginIp = loginIp;
	}
	
	
}
