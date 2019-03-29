/*
 * 唯有读书,不慵不扰
 * 
 */
package com.xiaoyu.simplenote.common.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.xiaoyu.simplenote.common.handler.EmailMqHandler;
import com.xiaoyu.simplenote.common.handler.HostRecorderMqHandler;
import com.xiaoyu.simplenote.common.handler.MessageMqHandler;

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
    private MessageMqHandler msgHandler;

    @Autowired
    private HostRecorderMqHandler hostHandler;

    @Autowired
    private EmailMqHandler emailHandler;

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        final MessageMqHandler msghandler = this.msgHandler;
        final HostRecorderMqHandler hostHandler = this.hostHandler;
        final EmailMqHandler emailHandler = this.emailHandler;
        hostHandler.consume();
        msghandler.consume();
        emailHandler.consume();
        LOG.info("消费者已启动,进行消息处理...");
    }

}
