package com.xiaoyu.modules.biz.message.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.xiaoyu.core.template.DefaultAbstractQueueTemplate;
import com.xiaoyu.modules.biz.message.dao.MessageDao;
import com.xiaoyu.modules.biz.message.entity.Message;

@Component
public class MessageHandler extends DefaultAbstractQueueTemplate {

	public MessageHandler() {
		super("xiaoyu.message");
	}

	@Autowired
	private MessageDao msgDao;

	@Override
	public void handleMessage(String message) {
		Message msg = JSON.parseObject(message, Message.class);
		if (msg != null) {
			this.msgDao.insert(msg);
		}
	}

}
