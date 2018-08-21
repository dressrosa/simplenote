/**
 * 
 */
package com.xiaoyu.common.handler;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.xiaoyu.core.template.DefaultAbstractQueueTemplate;
import com.xiaoyu.modules.common.MailBuilder;
import com.xiaoyu.modules.common.service.EmailService;
import com.xiaoyu.modules.constant.MqContant;

/**
 * 处理email
 * 
 * @author hongyu
 * @date 2018-08
 * @description
 */
@Component
public class EmailMqHandler extends DefaultAbstractQueueTemplate {

    public EmailMqHandler() {
        super(MqContant.EMAIL);
    }

    @Autowired
    private EmailService emailService;

    @Override
    public void handleMessage(String message) {
        MailBuilder email = JSON.parseObject(message, MailBuilder.class);
        try {
            if (email.getReceiverList().size() > 1) {
                this.emailService.sendEmail(email);
            } else {
                this.emailService.sendEmails(email);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
