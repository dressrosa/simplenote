package com.xiaoyu.modules.biz.message.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.xiaoyu.common.utils.IdGenerator;
import com.xiaoyu.core.template.DefaultAbstractQueueTemplate;
import com.xiaoyu.modules.biz.article.dao.ArticleDao;
import com.xiaoyu.modules.biz.article.entity.Article;
import com.xiaoyu.modules.biz.message.dao.MessageDao;
import com.xiaoyu.modules.biz.message.entity.Message;
import com.xiaoyu.modules.biz.user.dao.FollowDao;
import com.xiaoyu.modules.biz.user.dao.UserDao;
import com.xiaoyu.modules.biz.user.entity.Follow;
import com.xiaoyu.modules.biz.user.entity.User;

@Component
public class MessageHandler extends DefaultAbstractQueueTemplate {

    public MessageHandler() {
        super("xiaoyu.message");
    }

    @Autowired
    private MessageDao msgDao;
    @Autowired
    private ArticleDao articleDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private FollowDao followDao;

    @Override
    public void handleMessage(String message) {
        final Message msg = JSON.parseObject(message, Message.class);
        if (msg == null) {
            return;
        }
        final User u = this.userDao.getByUuid(msg.getSenderId());
        switch (msg.getType()) {
        // 消息
        case 0:
            // 文章
            if (msg.getBizType() == 0) {
                final Article ar = this.articleDao.getByUuid(msg.getBizId());
                if (ar == null) {
                    return;
                }
                msg.setReceiverId(ar.getUserId());
                // 评论
                if (msg.getBizAction() == 1) {
                    msg.setContent("用户" + u.getNickname() + "在文章“" + ar.getTitle() + "”中评论“" + msg.getContent() + "”");
                }
                // 点赞
                else if (msg.getBizAction() == 2) {
                    final Message t = new Message();
                    t.setSenderId(msg.getSenderId()).setBizId(msg.getBizId());
                    if (this.msgDao.isDoAgain(t) > 0) {
                        return;
                    }
                    msg.setContent("用户" + u.getNickname() + "赞了你的文章“" + ar.getTitle() + "”");
                }
                // 收藏
                else if (msg.getBizAction() == 3) {
                    final Message t = new Message();
                    t.setSenderId(msg.getSenderId()).setBizId(msg.getBizId());
                    if (this.msgDao.isDoAgain(t) > 0) {
                        return;
                    }
                    msg.setContent("用户" + u.getNickname() + "收藏了你的文章“" + ar.getTitle() + "”");
                }
                // 回复评论
                else if (msg.getBizAction() == 4) {
                    msg.setContent("用户" + u.getNickname() + "对你的评论进行了回复“" + msg.getContent() + "”");
                }
                // 评论@
                else if (msg.getBizAction() == 5) {
                    //TODO
                }
                // 评论点赞
                else if (msg.getBizAction() == 6) {
                    msg.setContent("用户" + u.getNickname() + "在文章“" + ar.getTitle() + "”" + "中赞了你的评论“" + msg.getContent()
                            + "”");
                }
            }
            // 人
            else if (msg.getBizType() == 1) {
                // 关注人
                if (msg.getBizAction() == 8) {
                    final Follow fo = this.followDao.getByUuid(msg.getBizId());
                    if (fo != null) {
                        msg.setReceiverId(fo.getUserId());
                    }
                    msg.setContent("用户" + u.getNickname() + "关注了你");
                }
            }
            break;
        // 留言
        case 1:
            msg.setContent("用户" + u.getNickname() + "给您留言“" + msg.getContent() + "”");
            break;
        // 通知
        case 2:
            // 文章
            if (msg.getBizType() == 0) {
                final Article ar = this.articleDao.getByUuid(msg.getBizId());
                if (ar == null) {
                    return;
                }
                msg.setContent("大家都在读这篇文章“" + ar.getTitle() + "”" + "快来围观吧");
            } else if (msg.getBizType() == 1) {// 人

            } else {
            }
            break;
        default:
            break;
        }
        msg.setUuid(IdGenerator.uuid());
        this.msgDao.insert(msg);
    }
}
