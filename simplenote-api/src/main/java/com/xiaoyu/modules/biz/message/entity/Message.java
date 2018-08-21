/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.message.entity;

import com.xiaoyu.common.base.BaseEntity;

/**
 * 2017年7月21日下午4:04:26
 * 
 * @author xiaoyu
 * @description 消息
 */
public class Message extends BaseEntity {

    private static final long serialVersionUID = 1L;

    // '消息类型 0消息 1留言 2通知'
    private int type;
    private String senderId;
    private String receiverId;
    // 业务类型 0文章 1用户'
    private int bizType;
    // '业务动作 0无 1评论 2赞 3收藏 4评论回复 5评论@ 6留言
    private int bizAction;
    // 业务id
    private String bizId;
    // 消息内容
    private String content;
    // 回复内容
    private String reply;
    private int isRead;

    public int getType() {
        return this.type;
    }

    public Message setType(int type) {
        this.type = type;
        return this;
    }

    public String getSenderId() {
        return this.senderId;
    }

    public Message setSenderId(String senderId) {
        this.senderId = senderId;
        return this;
    }

    public String getReceiverId() {
        return this.receiverId;
    }

    public Message setReceiverId(String receiverId) {
        this.receiverId = receiverId;
        return this;
    }

    public int getBizType() {
        return this.bizType;
    }

    public Message setBizType(int bizType) {
        this.bizType = bizType;
        return this;
    }

    public int getBizAction() {
        return this.bizAction;
    }

    public Message setBizAction(int bizAction) {
        this.bizAction = bizAction;
        return this;
    }

    public String getBizId() {
        return this.bizId;
    }

    public Message setBizId(String bizId) {
        this.bizId = bizId;
        return this;
    }

    public String getContent() {
        return this.content;
    }

    public Message setContent(String content) {
        this.content = content;
        return this;
    }

    public String getReply() {
        return this.reply;
    }

    public Message setReply(String reply) {
        this.reply = reply;
        return this;
    }

    public int getIsRead() {
        return this.isRead;
    }

    public Message setIsRead(int isRead) {
        this.isRead = isRead;
        return this;
    }

}
