/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.note.vo;

import com.xiaoyu.common.base.BaseEntity;

/**
 * @author hongyu
 * @date 2019-02
 * @description
 */
public class NoteCommentVo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String noteId;
    private String replyerName;
    private String replyerAvatar;
    private String authorId;
    private String parentId;
    private String content;
    private String replyerId;
    private String parentReplyerName;
    private String parentReplyerId;

    public String getNoteId() {
        return noteId;
    }

    public NoteCommentVo setNoteId(String noteId) {
        this.noteId = noteId;
        return this;
    }

    public String getReplyerName() {
        return replyerName;
    }

    public NoteCommentVo setReplyerName(String replyerName) {
        this.replyerName = replyerName;
        return this;
    }

    public String getReplyerAvatar() {
        return replyerAvatar;
    }

    public NoteCommentVo setReplyerAvatar(String replyerAvatar) {
        this.replyerAvatar = replyerAvatar;
        return this;
    }

    public String getAuthorId() {
        return authorId;
    }

    public NoteCommentVo setAuthorId(String authorId) {
        this.authorId = authorId;
        return this;
    }

    public String getParentId() {
        return parentId;
    }

    public NoteCommentVo setParentId(String parentId) {
        this.parentId = parentId;
        return this;
    }

    public String getContent() {
        return content;
    }

    public NoteCommentVo setContent(String content) {
        this.content = content;
        return this;
    }

    public String getReplyerId() {
        return replyerId;
    }

    public NoteCommentVo setReplyerId(String replyerId) {
        this.replyerId = replyerId;
        return this;
    }

    public String getParentReplyerName() {
        return parentReplyerName;
    }

    public NoteCommentVo setParentReplyerName(String parentReplyerName) {
        this.parentReplyerName = parentReplyerName;
        return this;
    }

    public String getParentReplyerId() {
        return parentReplyerId;
    }

    public NoteCommentVo setParentReplyerId(String parentReplyerId) {
        this.parentReplyerId = parentReplyerId;
        return this;
    }

}