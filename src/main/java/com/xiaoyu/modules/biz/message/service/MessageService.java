/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.message.service;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xiaoyu.common.base.ResponseMapper;
import com.xiaoyu.common.base.ResultConstant;
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

	/**
	 * 检查登录失效
	 */
	private User checkLoginDead(HttpServletRequest request) {
		String userId = request.getHeader("userId");
		String token = request.getHeader("token");
		HttpSession session = request.getSession(false);
		if (session == null)
			return null;
		User user = (User) session.getAttribute(token);
		if (user == null)
			return null;
		if (!userId.equals(user.getId()))
			return null;
		return user;
	}

	@Override
	public String getMsgByType(HttpServletRequest request, String userId, int type) {
		ResponseMapper mapper = ResponseMapper.createMapper();
		if (!userId.equals(request.getHeader("userId")))
			return mapper.setCode(ResultConstant.ARGS_ERROR).getResultJson();
		if (checkLoginDead(request) == null)
			return mapper.setCode(ResultConstant.LOGIN_INVALIDATE).getResultJson();
		Message t = new Message();
		t.setReceiverId(userId).setType(type);
		PageHelper.startPage(0, 10);
		Page<MessageVo> page = (Page<MessageVo>) this.msgDao.findVoByList(t);
		List<MessageVo> list = page.getResult();
		if (page != null && list != null && list.size() > 0) {
			for (MessageVo m : list) {
				User sender = this.userDao.getById(m.getSenderId());
				if (sender != null)
					m.setSenderName(sender.getNickname());
				if (m.getBizType() == 0) {
					Article ar = this.articleDao.getById(m.getBizId());
					if (ar != null) {
						m.setBizName(ar.getTitle());
					}
				} else if (m.getBizType() == 1) {
					User u = this.userDao.getById(m.getBizId());
					if (u != null) {
						m.setBizName(u.getNickname());
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

	@Override
	public String read(HttpServletRequest request, String msgIds) {
		ResponseMapper mapper = ResponseMapper.createMapper();
		if (checkLoginDead(request) == null)
			return mapper.setCode(ResultConstant.LOGIN_INVALIDATE).getResultJson();
		String[] ids = msgIds.split(";");
		if (ids != null && ids.length > 0) {
			List<String> idsList = Arrays.asList(ids);
			this.msgDao.read(idsList);
		}
		return mapper.getResultJson();
	}

	@Override
	public String unreadNum(HttpServletRequest request) {
		ResponseMapper mapper = ResponseMapper.createMapper();
		User u = checkLoginDead(request);
		if (u == null)
			return mapper.setCode(ResultConstant.LOGIN_INVALIDATE).getResultJson();
		int num = this.msgDao.getUnreadNumBefore1Hour(u.getId());
		if (num > 0)
			return mapper.setData(num).getResultJson();

		return mapper.setCode(ResultConstant.NOT_DATA).getResultJson();
	}

}
