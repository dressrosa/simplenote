package com.xiaoyu.common.event;

import org.springframework.context.ApplicationEvent;

/**
 * 发送邮件
 * 
 * @author hongyu
 * @date 2018-07
 * @description
 */
public class EmailEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    public EmailEvent(Object msg) {
        super(msg);
    }

}
