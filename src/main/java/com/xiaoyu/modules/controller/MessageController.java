package com.xiaoyu.modules.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xiaoyu.modules.biz.message.service.api.IMessageService;

@RestController
public class MessageController {

    @Autowired
    private IMessageService messageService;

    @RequestMapping(value = "api/v1/message/type/{type}", method = RequestMethod.GET)
    public String getMsgByType(HttpServletRequest request, @RequestParam(required = true) String userId,
            @PathVariable Integer type) {
        return this.messageService.getMsgByType(request, userId, type);
    }

    @RequestMapping(value = "api/v1/message/read", method = RequestMethod.POST)
    public String read(HttpServletRequest request, @RequestParam(required = true) String msgIds) {
        return this.messageService.read(request, msgIds);
    }

    @RequestMapping(value = "api/v1/message/unread-num", method = RequestMethod.GET)
    public String unreadNum(HttpServletRequest request) {
        return this.messageService.unreadNum(request);
    }
}
