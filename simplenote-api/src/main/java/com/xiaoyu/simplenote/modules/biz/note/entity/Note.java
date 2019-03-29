/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.simplenote.modules.biz.note.entity;

import com.xiaoyu.simplenote.common.base.BaseEntity;

/**
 * 纸条
 * 
 * @author hongyu
 * @date 2019-02
 * @description
 */
public class Note extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String userId;
    private String content;
    private String location;

    public String getUserId() {
        return userId;
    }

    public Note setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getContent() {
        return content;
    }

    public Note setContent(String content) {
        this.content = content;
        return this;
    }

    public String getLocation() {
        return location;
    }

    public Note setLocation(String location) {
        this.location = location;
        return this;
    }

}
