/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.message.vo;

import java.util.Date;

/**
 * 2017年7月21日下午4:04:26
 * 
 * @author xiaoyu
 * @description 消息
 */
public class MessageVo {

    private String messageId;
    private int type;// '消息类型 0消息 1留言 2通知'
    private String senderId;
    private String senderName;
    private String receiverId;
    private int bizType;// 业务类型 0文章 1用户'
    private int bizAction;// '业务动作 0无 1评论 2赞 3收藏 4评论回复 5评论@ 6留言
    private String bizId;// 业务id
    private String content;// 消息内容
    private String reply;// 回复内容
    private int isRead;
    private Date createDate;

    private String bizName;// 业务名称

    public Date getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getMessageId() {
        return this.messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSenderId() {
        return this.senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return this.senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverId() {
        return this.receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public int getBizType() {
        return this.bizType;
    }

    public void setBizType(int bizType) {
        this.bizType = bizType;
    }

    public int getBizAction() {
        return this.bizAction;
    }

    public void setBizAction(int bizAction) {
        this.bizAction = bizAction;
    }

    public String getBizId() {
        return this.bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReply() {
        return this.reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public int getIsRead() {
        return this.isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public String getBizName() {
        return this.bizName;
    }

    public void setBizName(String bizName) {
        this.bizName = bizName;
    }

}
