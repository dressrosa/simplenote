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
		Message msg = JSON.parseObject(message, Message.class);
		if (msg == null)
			return;
		User u = this.userDao.getById(msg.getSenderId());
		switch (msg.getType()) {
		case 0:// 消息
			if (msg.getBizType() == 0) {// 文章
				Article ar = this.articleDao.getById(msg.getBizId());
				if (ar == null)
					return;
				msg.setReceiverId(ar.getUserId());
				if (msg.getBizAction() == 1) {// 评论
					msg.setContent(
							"用户" + u.getNickname() + "在文章“" + ar.getTitle() + "”中评论“" + msg.getContent() + "”");
				} else if (msg.getBizAction() == 2) {// 点赞
					Message t = new Message();
					t.setSenderId(msg.getSenderId()).setBizId(msg.getBizId());
					if (this.msgDao.isDoAgain(t) > 0)// 对于点赞后取消,然后又点赞的
						return;
					msg.setContent("用户" + u.getNickname() + "赞了你的文章“" + ar.getTitle() + "”");
				} else if (msg.getBizAction() == 3) {// 收藏
					Message t = new Message();
					t.setSenderId(msg.getSenderId()).setBizId(msg.getBizId());
					if (this.msgDao.isDoAgain(t) > 0)// 对于收藏后取消,然后又收藏的
						return;
					msg.setContent("用户" + u.getNickname() + "收藏了你的文章“" + ar.getTitle() + "”");
				} else if (msg.getBizAction() == 4) {// 回复评论
					msg.setContent("用户" + u.getNickname() + "对你的评论进行了回复“" + msg.getContent() + "”");
				} else if (msg.getBizAction() == 5) {// 评论@

				} else if (msg.getBizAction() == 6) {// 赞评论
					msg.setContent("用户" + u.getNickname() + "在文章“" + ar.getTitle() + "”" + "中赞了你的评论“"
							+ msg.getContent() + "”");
				}
			} else if (msg.getBizType() == 1) {// 人
				if (msg.getBizAction() == 8) {// 关注人
					Follow fo = this.followDao.getById(msg.getBizId());
					if (fo != null) {
						msg.setReceiverId(fo.getUserId());
					}
					msg.setContent("用户" + u.getNickname() + "关注了你");
				}
			}
			break;
		case 1:// 留言
			msg.setContent("用户" + u.getNickname() + "给您留言“" + msg.getContent() + "”");
			break;
		case 2:// 通知
			if (msg.getBizType() == 0) {// 文章
				Article ar = this.articleDao.getById(msg.getBizId());
				if (ar == null)
					return;
				msg.setContent("大家都在读这篇文章“" + ar.getTitle() + "”" + "快来围观吧");
			} else if (msg.getBizType() == 1) {// 人

			} else {
			}
			break;
		}
		msg.setId(IdGenerator.uuid());
		this.msgDao.insert(msg);
	}
}
