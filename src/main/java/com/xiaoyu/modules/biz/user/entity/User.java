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
	
	private String name;
	private String img;
	private MultipartFile imgFile;
	private String description;
	
	
	
	
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
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
