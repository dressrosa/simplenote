/*
 * 唯有读书,不慵不扰
 * 
 */
package com.xiaoyu.common.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.xiaoyu.modules.biz.message.service.MessageHandler;
import com.xiaoyu.modules.common.HostRecorderHandler;

/**
 * 2017年3月31日下午4:31:40
 * 
 * @author xiaoyu
 * @description 启动时执行消费者进行消费
 */
@Component
public class MqConsumerListener implements ApplicationListener<ApplicationReadyEvent> {

    private final static Logger LOG = LoggerFactory.getLogger(MqConsumerListener.class);
    
    @Autowired
    private MessageHandler msgHandler;

    @Autowired
    private HostRecorderHandler hostHandler;

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        final MessageHandler msghandler = this.msgHandler;
        final HostRecorderHandler hostHandler = this.hostHandler;
        hostHandler.consume();
        msghandler.consume();
        LOG.info("消费者已启动,进行消息处理...");
    }

}
