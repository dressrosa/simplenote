package com.xiaoyu.modules.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xiaoyu.common.request.TraceRequest;
import com.xiaoyu.common.util.Utils;
import com.xiaoyu.modules.biz.message.entity.Message;
import com.xiaoyu.modules.biz.message.service.api.IMessageService;

/**
 * @author hongyu
 * @date 2018-02
 * @description
 */
@RestController
public class MessageController {

    @Autowired
    private IMessageService messageService;

    @RequestMapping(value = "api/v1/message/type/{type}", method = RequestMethod.GET)
    public String getMsgByType(HttpServletRequest request, @RequestParam(required = true) String userId,
            @PathVariable Integer type) {
        TraceRequest req = Utils.getTraceRequest(request);
        return this.messageService.getMsgByType(req, userId, type);
    }

    @RequestMapping(value = "api/v1/message/read", method = RequestMethod.POST)
    public String read(HttpServletRequest request, @RequestParam(required = true) String msgIds) {
        TraceRequest req = Utils.getTraceRequest(request);
        return this.messageService.read(req, msgIds);
    }

    @RequestMapping(value = "api/v1/message/unread-num", method = RequestMethod.GET)
    public String unreadNum(HttpServletRequest request) {
        TraceRequest req = Utils.getTraceRequest(request);
        return this.messageService.unreadNum(req);
    }

    @RequestMapping(value = "api/v1/message/reply", method = RequestMethod.POST)
    public String replyMsg(HttpServletRequest request, @RequestParam(required = true) String msgId,
            @RequestParam(required = true) String replyContent) {
        TraceRequest req = Utils.getTraceRequest(request);
        return this.messageService.replyMsg(req, msgId, replyContent);
    }

    @RequestMapping(value = "api/v1/message/remove", method = RequestMethod.POST)
    public String remove(HttpServletRequest request, @RequestParam(required = true) Message... messages) {
        TraceRequest req = Utils.getTraceRequest(request);
        return this.messageService.removeMessage(req, messages);
    }
}
