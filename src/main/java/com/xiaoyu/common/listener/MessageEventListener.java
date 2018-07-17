package com.xiaoyu.common.listener;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.xiaoyu.common.event.EmailEvent;
import com.xiaoyu.common.event.MessageEvent;
import com.xiaoyu.modules.biz.message.entity.Message;
import com.xiaoyu.modules.biz.message.service.MessageHandler;
import com.xiaoyu.modules.biz.user.service.UserServiceImpl;
import com.xiaoyu.modules.common.EmailService;
import com.xiaoyu.modules.common.MailBuilder;

/**
 * @author xiaoyu
 * @date 2018-01
 * @description 消息事件监听
 */
@Component
public class MessageEventListener implements ApplicationListener<ApplicationEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private MessageHandler msgHandler;

    @Autowired
    private EmailService emailService;

    /**
     *  事件处理底层是和业务接口共用一个线程 ,这里防止mq连接异常,导致阻塞,采用异步方式
     */
    @Async
    @Override
    public void onApplicationEvent(final ApplicationEvent event) {
        if (event instanceof MessageEvent) {
            MessageEvent msgEvent = (MessageEvent) event;
            Message msg = (Message) msgEvent.getSource();
            try {
                this.msgHandler.produce(JSON.toJSONString(msg));
            } catch (Exception e) {
                LOG.error(e.toString());
            }
        } else if (event instanceof EmailEvent) {
            EmailEvent emailEvent = (EmailEvent) event;
            MailBuilder builder = (MailBuilder) emailEvent.getSource();
            try {
                if (builder.getReceiverList().size() > 1) {
                    this.emailService.sendEmail(builder);
                } else {
                    this.emailService.sendEmails(builder);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
