/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.message.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.xiaoyu.beacon.autoconfigure.anno.BeaconExporter;
import com.xiaoyu.common.base.ResponseCode;
import com.xiaoyu.common.base.ResponseMapper;
import com.xiaoyu.common.request.TraceRequest;
import com.xiaoyu.core.constant.Type;
import com.xiaoyu.core.template.BaseProducer;
import com.xiaoyu.modules.biz.article.entity.Article;
import com.xiaoyu.modules.biz.article.service.api.IArticleService;
import com.xiaoyu.modules.biz.message.dao.MessageDao;
import com.xiaoyu.modules.biz.message.entity.Message;
import com.xiaoyu.modules.biz.message.service.api.IMessageService;
import com.xiaoyu.modules.biz.message.vo.MessageVo;
import com.xiaoyu.modules.biz.user.entity.User;
import com.xiaoyu.modules.biz.user.service.api.IUserService;
import com.xiaoyu.modules.constant.BizAction;
import com.xiaoyu.modules.constant.MqContant;

/**
 * @author hongyu
 * @date 2018-02
 * @description
 */
@Service
@Primary
@BeaconExporter(interfaceName = "com.xiaoyu.modules.biz.message.service.api.IMessageService", group = "dev")
public class MessageServiceImpl implements IMessageService {

    @Autowired
    private MessageDao msgDao;

    @Autowired
    private IUserService userService;

    @Autowired
    private IArticleService articleService;

    @Override
    public ResponseMapper getMsgByType(TraceRequest request, String userId, int type) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        if (!request.isLogin()) {
            return mapper.code(ResponseCode.LOGIN_INVALIDATE.statusCode());
        }
        User user = request.getUser();
        if (!userId.equals(user.getUuid())) {
            return mapper.code(ResponseCode.ARGS_ERROR.statusCode());
        }
        Message t = new Message();
        t.setReceiverId(userId).setType(type);

        PageHelper.startPage(1, 12);
        List<MessageVo> list = this.msgDao.findVoByList(t);

        if (list.isEmpty()) {
            return mapper.code(ResponseCode.NO_DATA.statusCode());
        }
        Article tarticle = null;
        User tuser = null;
        for (MessageVo m : list) {
            if (m.getBizType() == 0) {
                tarticle = this.articleService.getByUuid(m.getBizId());
                m.setBizName(tarticle != null ? tarticle.getTitle() : "");
            } else if (m.getBizType() == 1) {
                tuser = this.userService.getByUuid(m.getBizId());
                m.setBizName(tuser != null ? tuser.getNickname() : "");
            }
        }
        return mapper.data(list);
    }

    @Override
    public ResponseMapper replyMsg(TraceRequest request, String msgId, String replyContent) {
        // TODO 1.回复单纯的消息 2.回复评论等,应该调用回复评论等相应的接口 目前回复消息只有回复评论
        Message msg = this.msgDao.getByUuid(msgId);
        if (msg == null) {
            ResponseMapper.createMapper().code(ResponseCode.NO_DATA.statusCode());
        }

        @SuppressWarnings("null")
        int bizAction = msg.getBizAction();
        // 评论相关
        if (bizAction == BizAction.COMMENT.statusCode()
                || bizAction == BizAction.COMMENT_AT.statusCode()
                || bizAction == BizAction.REPLY.statusCode()) {
            return this.articleService.reply(request, msg.getBizId(), replyContent);
        }
        return ResponseMapper.createMapper();
    }

    @Override
    public ResponseMapper read(TraceRequest request, String msgIds) {
        if (!request.isLogin()) {
            return ResponseMapper.createMapper().code(ResponseCode.LOGIN_INVALIDATE.statusCode());
        }
        String[] ids = msgIds.split(";");
        if (ids != null && ids.length > 0) {
            List<String> idsList = Arrays.asList(ids);
            this.msgDao.read(idsList);
        }
        return ResponseMapper.createMapper();
    }

    @Override
    public ResponseMapper unreadNum(TraceRequest request) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        if (!request.isLogin()) {
            return mapper.code(ResponseCode.LOGIN_INVALIDATE.statusCode());
        }
        User u = request.getUser();
        int num = this.msgDao.getUnreadNumBefore1Hour(u.getUuid());
        if (num > 0) {
            return mapper.data(num);
        }
        return mapper.code(ResponseCode.NO_DATA.statusCode());
    }

    @Override
    public ResponseMapper sendMsgEvent(final Message message) {
        Message msg = new Message();
        msg.setSenderId(message.getSenderId())
                .setReceiverId(message.getReceiverId())
                .setType(message.getType())
                .setBizId(message.getBizId())
                .setBizType(message.getBizType())
                .setBizAction(message.getBizAction());
        if (message.getContent() != null) {
            msg.setContent(message.getContent());
        }
        if (message.getReply() != null) {
            msg.setReply(message.getReply());
        }

        try {
            BaseProducer.produce(Type.QUEUE, MqContant.MESSAGE, JSON.toJSONString(message));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ResponseMapper removeMessage(TraceRequest request, Message... messages) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        if (!request.isLogin()) {
            return mapper.code(ResponseCode.LOGIN_INVALIDATE.statusCode());
        }
        User u = request.getUser();
        if (messages.length == 1) {
            Message temp = new Message();
            temp.setReceiverId(u.getUuid()).setUuid(messages[0].getUuid());
            this.msgDao.delete(temp);
        }
        this.msgDao.batchDelete(Arrays.asList(messages));
        return mapper;
    }

    @Override
    public int isDoAgain(Message msg) {
        return this.msgDao.isDoAgain(msg);
    }

    @Override
    public int insert(Message msg) {
        return this.msgDao.insert(msg);
    }

}
