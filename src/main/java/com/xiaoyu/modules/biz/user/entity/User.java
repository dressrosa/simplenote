/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */ 
package com.xiaoyu.modules.biz.user.entity;


import org.springframework.web.multipart.MultipartFile;

import com.xiaoyu.common.base.BaseEntity;

/**
 * @author xiaoyu
 *2016年3月16日
 */
public class User extends BaseEntity{
	
	private static final long serialVersionUID = 1L;
	
	private String nickName;
	private String loginName;
	private String img;
	private MultipartFile imgFile;
	private String description;
	private String password;

	
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public MultipartFile getImgFile() {
		return imgFile;
	}
	public void setImgFile(MultipartFile imgFile) {
		this.imgFile = imgFile;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	
	
	
	
}
