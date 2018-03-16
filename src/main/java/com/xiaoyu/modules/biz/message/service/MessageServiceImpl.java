/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.message.service;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.xiaoyu.common.base.ResponseCode;
import com.xiaoyu.common.base.ResponseMapper;
import com.xiaoyu.common.event.MessageEvent;
import com.xiaoyu.common.utils.UserUtils;
import com.xiaoyu.modules.biz.article.dao.ArticleDao;
import com.xiaoyu.modules.biz.article.entity.Article;
import com.xiaoyu.modules.biz.article.service.api.IArticleService;
import com.xiaoyu.modules.biz.message.dao.MessageDao;
import com.xiaoyu.modules.biz.message.entity.Message;
import com.xiaoyu.modules.biz.message.service.api.IMessageService;
import com.xiaoyu.modules.biz.message.vo.MessageVo;
import com.xiaoyu.modules.biz.user.dao.UserDao;
import com.xiaoyu.modules.biz.user.entity.User;
import com.xiaoyu.modules.constant.BizAction;

/**
 * @author hongyu
 * @date 2018-02
 * @description
 */
@Service
@Primary
public class MessageServiceImpl implements IMessageService {

    @Autowired
    private MessageDao msgDao;

    @Autowired
    private ArticleDao articleDao;
    @Autowired
    private UserDao userDao;

    @Autowired
    private IArticleService articleService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public String getMsgByType(HttpServletRequest request, String userId, int type) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        if (!userId.equals(request.getHeader("userId"))) {
            return mapper.code(ResponseCode.ARGS_ERROR.statusCode()).resultJson();
        }
        if (UserUtils.checkLoginDead(request) == null) {
            return mapper.code(ResponseCode.LOGIN_INVALIDATE.statusCode()).resultJson();
        }
        Message t = new Message();
        t.setReceiverId(userId).setType(type);
        PageHelper.startPage(1, 12);
        List<MessageVo> list = this.msgDao.findVoByList(t);

        if (list.isEmpty()) {
            return mapper.code(ResponseCode.NO_DATA.statusCode()).resultJson();
        }
        Article tarticle = null;
        User tuser = null;
        for (final MessageVo m : list) {
            if (m.getBizType() == 0) {
                tarticle = this.articleDao.getByUuid(m.getBizId());
                m.setBizName(tarticle != null ? tarticle.getTitle() : "");
            } else if (m.getBizType() == 1) {
                tuser = this.userDao.getByUuid(m.getBizId());
                m.setBizName(tuser != null ? tuser.getNickname() : "");
            }
        }
        return mapper.data(list).resultJson();
    }

    @Override
    public String replyMsg(HttpServletRequest request, String msgId, String replyContent) {
        // TODO 1.回复单纯的消息 2.回复评论等,应该调用回复评论等相应的接口 目前回复消息只有回复评论
        ResponseMapper mapper = ResponseMapper.createMapper();
        Message msg = this.msgDao.getByUuid(msgId);
        if (msg == null) {
            mapper.code(ResponseCode.NO_DATA.statusCode()).resultJson();
        }

        @SuppressWarnings("null")
        int bizAction = msg.getBizAction();
        // 评论相关
        if (bizAction == BizAction.COMMENT.statusCode()
                || bizAction == BizAction.COMMENT_AT.statusCode()
                || bizAction == BizAction.REPLY.statusCode()) {
            return this.articleService.reply(request, msg.getBizId(), replyContent);
        }
        return mapper.resultJson();
    }

    @Override
    public String read(HttpServletRequest request, String msgIds) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        if (UserUtils.checkLoginDead(request) == null) {
            return mapper.code(ResponseCode.LOGIN_INVALIDATE.statusCode()).resultJson();
        }
        String[] ids = msgIds.split(";");
        if (ids != null && ids.length > 0) {
            List<String> idsList = Arrays.asList(ids);
            this.msgDao.read(idsList);
        }
        return mapper.resultJson();
    }

    @Override
    public String unreadNum(HttpServletRequest request) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        User u = UserUtils.checkLoginDead(request);
        if (u == null) {
            return mapper.code(ResponseCode.LOGIN_INVALIDATE.statusCode()).resultJson();
        }
        int num = this.msgDao.getUnreadNumBefore1Hour(u.getUuid());
        if (num > 0) {
            return mapper.data(num).resultJson();
        }
        return mapper.code(ResponseCode.NO_DATA.statusCode()).resultJson();
    }

    @Override
    public String sendMsgEvent(final Message message) {
        final Message msg = new Message();
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
        this.eventPublisher.publishEvent(new MessageEvent(msg));
        return null;
    }

    @Override
    public String removeMessage(HttpServletRequest request, Message... messages) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        User u = UserUtils.checkLoginDead(request);
        if (u == null) {
            return mapper.code(ResponseCode.LOGIN_INVALIDATE.statusCode()).resultJson();
        }
        if (messages.length == 1) {
            Message temp = new Message();
            temp.setReceiverId(u.getUuid()).setUuid(messages[0].getUuid());
            this.msgDao.delete(temp);
        }
        this.msgDao.batchDelete(Arrays.asList(messages));
        return mapper.resultJson();
    }

}
