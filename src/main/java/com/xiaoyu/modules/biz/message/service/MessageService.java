/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.message.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xiaoyu.common.base.ResponseMapper;
import com.xiaoyu.modules.biz.article.dao.ArticleDao;
import com.xiaoyu.modules.biz.article.entity.Article;
import com.xiaoyu.modules.biz.message.dao.MessageDao;
import com.xiaoyu.modules.biz.message.entity.Message;
import com.xiaoyu.modules.biz.message.service.api.IMessageService;
import com.xiaoyu.modules.biz.message.vo.MessageVo;
import com.xiaoyu.modules.biz.user.dao.UserDao;
import com.xiaoyu.modules.biz.user.entity.User;

@Service
public class MessageService implements IMessageService {

	@Autowired
	private MessageDao msgDao;

	@Autowired
	private ArticleDao articleDao;
	@Autowired
	private UserDao userDao;

	@Override
	public String getMsgByType(HttpServletRequest request, String userId, int type) {
		ResponseMapper mapper = ResponseMapper.createMapper();
		Message t = new Message();
		t.setReceiverId(userId).setType(type);
		PageHelper.startPage(0, 10);
		Page<MessageVo> page = (Page<MessageVo>) this.msgDao.findVoByList(t);
		List<MessageVo> list = page.getResult();
		if (page != null && list != null && list.size() > 0) {
			for (MessageVo m : list) {
				if (m.getBizType() == 0) {
					Article ar = this.articleDao.getById(m.getBizId());
					if (ar != null) {
						m.setBizName(ar.getTitle());
					}
				} else if (m.getBizType() == 1) {
					User ar = this.userDao.getById(m.getBizId());
					if (ar != null) {
						m.setBizName(ar.getNickname());
					}
				}
			}
		}
		return mapper.setData(list).getResultJson();
	}

	@Override
	public String removeMsg(HttpServletRequest request, String msgId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String replyMsg(HttpServletRequest request, String msgId, String reply) {
		// TODO Auto-generated method stub
		return null;
	}

}
