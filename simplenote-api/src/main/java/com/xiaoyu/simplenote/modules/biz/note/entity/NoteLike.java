/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.simplenote.modules.biz.note.entity;

import com.xiaoyu.simplenote.common.base.BaseEntity;

/**
 * @author hongyu
 * @date 2019-02
 * @description
 */
public class NoteLike extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String noteId;
    private String userId;
    // 点了几次
    private Integer num;
    // -1 未点赞 1点赞
    private Integer status;

    public Integer getStatus() {
        return this.status;
    }

    public NoteLike setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public String getNoteId() {
        return noteId;
    }

    public NoteLike setNoteId(String noteId) {
        this.noteId = noteId;
        return this;
    }

    public String getUserId() {
        return this.userId;
    }

    public NoteLike setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public Integer getNum() {
        return this.num;
    }

    public NoteLike setNum(Integer num) {
        this.num = num;
        return this;
    }

}
