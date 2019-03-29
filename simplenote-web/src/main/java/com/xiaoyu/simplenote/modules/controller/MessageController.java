package com.xiaoyu.simplenote.modules.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.xiaoyu.simplenote.common.base.ResponseCode;
import com.xiaoyu.simplenote.common.base.ResponseMapper;
import com.xiaoyu.simplenote.common.request.TraceRequest;
import com.xiaoyu.simplenote.common.util.Utils;
import com.xiaoyu.simplenote.modules.biz.message.entity.Message;
import com.xiaoyu.simplenote.modules.biz.message.service.api.IMessageService;

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
    public String getMsgByType(HttpServletRequest request, String userId, @PathVariable Integer type) {
        TraceRequest req = Utils.getTraceRequest(request);
        return this.messageService.getMsgByType(req, userId, type).resultJson();
    }

    @RequestMapping(value = "api/v1/message/read", method = RequestMethod.POST)
    public String read(HttpServletRequest request, @RequestBody JSONObject json) {
        if (!json.containsKey("msgIds")) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.ARGS_ERROR.statusCode()).resultJson();
        }
        TraceRequest req = Utils.getTraceRequest(request);
        return this.messageService.read(req, json.getString("msgIds")).resultJson();
    }

    @RequestMapping(value = "api/v1/message/unread-num", method = RequestMethod.GET)
    public String unreadNum(HttpServletRequest request) {
        TraceRequest req = Utils.getTraceRequest(request);
        return this.messageService.unreadNum(req).resultJson();
    }

    @RequestMapping(value = "api/v1/message/reply", method = RequestMethod.POST)
    public String replyMsg(HttpServletRequest request, @RequestBody JSONObject json) {
        if (!json.containsKey("msgId") || !json.containsKey("replyContent")) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.ARGS_ERROR.statusCode()).resultJson();
        }
        TraceRequest req = Utils.getTraceRequest(request);
        if (!req.isLogin()) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.LOGIN_INVALIDATE.statusCode())
                    .resultJson();
        }
        return this.messageService.replyMsg(req, json.getString("msgId"), json.getString("replyContent")).resultJson();
    }

    @RequestMapping(value = "api/v1/message/remove", method = RequestMethod.POST)
    public String remove(HttpServletRequest request, @RequestBody Message... messages) {
        TraceRequest req = Utils.getTraceRequest(request);
        if (!req.isLogin()) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.LOGIN_INVALIDATE.statusCode())
                    .resultJson();
        }
        return this.messageService.removeMessage(req, messages).resultJson();
    }
}
