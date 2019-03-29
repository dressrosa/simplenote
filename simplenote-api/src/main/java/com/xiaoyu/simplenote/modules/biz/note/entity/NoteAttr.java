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
public class NoteAttr extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String noteId;
    private Integer readNum;
    private Integer commentNum;
    private Integer markNum;
    private Integer likeNum;

    public String getNoteId() {
        return noteId;
    }

    public NoteAttr setNoteId(String noteId) {
        this.noteId = noteId;
        return this;
    }

    public Integer getMarkNum() {
        return markNum;
    }

    public NoteAttr setMarkNum(Integer markNum) {
        this.markNum = markNum;
        return this;
    }

    public Integer getReadNum() {
        return this.readNum;
    }

    public NoteAttr setReadNum(Integer readNum) {
        this.readNum = readNum;
        return this;
    }

    public Integer getCommentNum() {
        return this.commentNum;
    }

    public NoteAttr setCommentNum(Integer commentNum) {
        this.commentNum = commentNum;
        return this;
    }

    public Integer getLikeNum() {
        return this.likeNum;
    }

    public NoteAttr setLikeNum(Integer likeNum) {
        this.likeNum = likeNum;
        return this;
    }

}
