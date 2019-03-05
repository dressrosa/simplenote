/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.note.entity;

import com.xiaoyu.common.base.BaseEntity;

/**
 * @author hongyu
 * @date 2019-02
 * @description
 */
public class NoteComment extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String noteId;
    private String authorId;
    private String parentId;
    private String parentReplyerId;
    private String content;
    private String replyerId;

    public String getParentReplyerId() {
        return this.parentReplyerId;
    }

    public NoteComment setParentReplyerId(String parentReplyerId) {
        this.parentReplyerId = parentReplyerId;
        return this;
    }

    public String getNoteId() {
        return noteId;
    }

    public NoteComment setNoteId(String noteId) {
        this.noteId = noteId;
        return this;
    }

    public String getAuthorId() {
        return this.authorId;
    }

    public NoteComment setAuthorId(String authorId) {
        this.authorId = authorId;
        return this;
    }

    public String getParentId() {
        return this.parentId;
    }

    public NoteComment setParentId(String parentId) {
        this.parentId = parentId;
        return this;
    }

    public String getContent() {
        return this.content;
    }

    public NoteComment setContent(String content) {
        this.content = content;
        return this;
    }

    public String getReplyerId() {
        return this.replyerId;
    }

    public NoteComment setReplyerId(String replyerId) {
        this.replyerId = replyerId;
        return this;
    }

}