/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.message.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.xiaoyu.modules.biz.message.service.api.IMessageService;

@Service
public class MessageService implements IMessageService{

	@Override
	public String getMsgByType(HttpServletRequest request, String userId, int type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String removeMsg(HttpServletRequest request, String msgId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String replyMsg(HttpServletRequest request, String msgId, String reply) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
}
