/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */ 
package com.xiaoyu.modules.sys.upload.entity;

import com.xiaoyu.common.base.BaseEntity;

/**
 * @author xiaoyu
 *2016年3月29日
 */
public class FileEntity extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String thumbnailFileName;
	private String newFilename;
	private String contentType;
	private String size;
	private String thumbnailSize;
	private String path;
	private String thumbnailUrl;
	private String url;
	
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getThumbnailUrl() {
		return thumbnailUrl;
	}
	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getThumbnailFileName() {
		return thumbnailFileName;
	}
	public void setThumbnailFileName(String thumbnailFileName) {
		this.thumbnailFileName = thumbnailFileName;
	}
	public String getNewFilename() {
		return newFilename;
	}
	public void setNewFilename(String newFilename) {
		this.newFilename = newFilename;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getThumbnailSize() {
		return thumbnailSize;
	}
	public void setThumbnailSize(String thumbnailSize) {
		this.thumbnailSize = thumbnailSize;
	}
	
	
	
	
}
