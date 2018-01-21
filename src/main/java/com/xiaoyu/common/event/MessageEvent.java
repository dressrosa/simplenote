package com.xiaoyu.common.event;

import org.springframework.context.ApplicationEvent;

import com.xiaoyu.modules.biz.message.entity.Message;

/**
 * @author xiaoyu
 * @date 2018-01
 * @description 消息事件
 */
public class MessageEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;
    
    public MessageEvent(Message msg) {
        super(msg);
    }

}
