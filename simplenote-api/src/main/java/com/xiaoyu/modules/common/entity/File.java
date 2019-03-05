/**
 * 
 */
package com.xiaoyu.modules.common.entity;

import com.xiaoyu.common.base.BaseEntity;

/**
 * @author hongyu
 * @date 2019-02
 * @description
 */
public class File extends BaseEntity {

    private static final long serialVersionUID = 4654397893096635292L;

    private int bizType;
    private String bizId;
    private int fileType;
    private String userId;
    private String name;
    private String url;

    public int getBizType() {
        return bizType;
    }

    public File setBizType(int bizType) {
        this.bizType = bizType;
        return this;
    }

    public String getBizId() {
        return bizId;
    }

    public File setBizId(String bizId) {
        this.bizId = bizId;
        return this;
    }

    public int getFileType() {
        return fileType;
    }

    public File setFileType(int fileType) {
        this.fileType = fileType;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public File setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getName() {
        return name;
    }

    public File setName(String name) {
        this.name = name;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public File setUrl(String url) {
        this.url = url;
        return this;
    }

}
