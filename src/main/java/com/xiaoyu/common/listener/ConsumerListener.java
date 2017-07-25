/*
 * 唯有读书,不慵不扰
 * 
 */
package com.xiaoyu.common.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.xiaoyu.modules.biz.message.service.MessageHandler;

/**
 * 2017年3月31日下午4:31:40
 * 
 * @author xiaoyu
 * @description 启动时执行消费者进行消费
 */
@Component
public class ConsumerListener implements ApplicationListener<ApplicationReadyEvent> {

	@Autowired
	private MessageHandler msgHandler;

	@Override
	public void onApplicationEvent(final ApplicationReadyEvent event) {
		final MessageHandler handler = msgHandler;
		handler.consume();
		System.out.println("消费者已启动,进行消息处理...");
	}

}
