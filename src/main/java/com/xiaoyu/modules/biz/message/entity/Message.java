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

	private int type;// '消息类型 0消息 1留言 2通知'
	private String senderId;
	private String receiverId;
	private int bizType;// 业务类型 0文章 1用户'
	private int bizAction;// '业务动作 0无 1评论 2赞 3收藏 4评论回复 5评论@ 6留言
	private String bizId;// 业务id
	private String content;// 消息内容
	private String reply;// 回复内容
	private int isRead;

	public int getType() {
		return type;
	}

	public Message setType(int type) {
		this.type = type;
		return this;
	}

	public String getSenderId() {
		return senderId;
	}

	public Message setSenderId(String senderId) {
		this.senderId = senderId;
		return this;
	}

	public String getReceiverId() {
		return receiverId;
	}

	public Message setReceiverId(String receiverId) {
		this.receiverId = receiverId;
		return this;
	}

	public int getBizType() {
		return bizType;
	}

	public Message setBizType(int bizType) {
		this.bizType = bizType;
		return this;
	}

	public int getBizAction() {
		return bizAction;
	}

	public Message setBizAction(int bizAction) {
		this.bizAction = bizAction;
		return this;
	}

	public String getBizId() {
		return bizId;
	}

	public Message setBizId(String bizId) {
		this.bizId = bizId;
		return this;
	}

	public String getContent() {
		return content;
	}

	public Message setContent(String content) {
		this.content = content;
		return this;
	}

	public String getReply() {
		return reply;
	}

	public Message setReply(String reply) {
		this.reply = reply;
		return this;
	}

	public int getIsRead() {
		return isRead;
	}

	public Message setIsRead(int isRead) {
		this.isRead = isRead;
		return this;
	}

}
