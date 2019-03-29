/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.simplenote.modules.biz.note.entity;

import java.util.Date;

import com.xiaoyu.simplenote.common.base.BaseEntity;

/**
 * 纸条目标
 * 
 * @author hongyu
 * @date 2019-02
 * @description
 */
public class NoteDestination extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String noteId;
    private String notePiece;
    private String reader;
    private Date readTime;
    private Integer state;
    private String user;

    public String getNoteId() {
        return noteId;
    }

    public NoteDestination setNoteId(String noteId) {
        this.noteId = noteId;
        return this;
    }

    public String getNotePiece() {
        return notePiece;
    }

    public NoteDestination setNotePiece(String notePiece) {
        this.notePiece = notePiece;
        return this;
    }

    public String getReader() {
        return reader;
    }

    public NoteDestination setReader(String reader) {
        this.reader = reader;
        return this;
    }

    public Date getReadTime() {
        return readTime;
    }

    public NoteDestination setReadTime(Date readTime) {
        this.readTime = readTime;
        return this;
    }

    public Integer getState() {
        return state;
    }

    public NoteDestination setState(Integer state) {
        this.state = state;
        return this;
    }

    public String getUser() {
        return user;
    }

    public NoteDestination setUser(String user) {
        this.user = user;
        return this;
    }

}
