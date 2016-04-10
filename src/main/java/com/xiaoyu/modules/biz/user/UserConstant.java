/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */ 
package com.xiaoyu.modules.biz.user;

/**用户信息的一些常量
 * @author xiaoyu
 *2016年4月9日
 */
public enum UserConstant {
	
	
	/**
	 * 密码不正确
	 */
	WRONGPWD("密码不正确"),
	/**
	 * 用户不存在
	 */
	NoUser("用户不存在");

	private String message;
	private UserConstant() {
		
	}
	private UserConstant(String  message) {
		this.message = message;
	}
	@Override
	public String toString() {
		return this.message;
				
	}
	
	
}
