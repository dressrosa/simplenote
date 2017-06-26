/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.user.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xiaoyu.common.base.BaseService;
import com.xiaoyu.common.base.ResponseMapper;
import com.xiaoyu.common.base.ResultConstant;
import com.xiaoyu.common.utils.IdGenerator;
import com.xiaoyu.common.utils.Md5Utils;
import com.xiaoyu.common.utils.StringUtils;
import com.xiaoyu.modules.biz.user.dao.LoginRecordDao;
import com.xiaoyu.modules.biz.user.dao.UserAttrDao;
import com.xiaoyu.modules.biz.user.dao.UserDao;
import com.xiaoyu.modules.biz.user.entity.LoginRecord;
import com.xiaoyu.modules.biz.user.entity.User;
import com.xiaoyu.modules.biz.user.entity.UserAttr;

/**
 * @author xiaoyu 2016年3月16日
 */
@Service
@Transactional
public class UserService extends BaseService<UserDao, User> implements IUserService {

	@Autowired
	private UserDao userDao;

	@Autowired
	private LoginRecordDao userRecordDao;

	private Map<String, Object> user2Map(User u) {
		Map<String, Object> map = new HashMap<>();
		map.put("userId", u.getId());
		map.put("description", u.getDescription());
		map.put("avatar", u.getAvatar());
		map.put("nickname", u.getNickname());
		map.put("signature", u.getSignature());
		map.put("attr", u.getAttr());
		return map;
	}

	private User getForLogin(User user) {
		User u = this.userDao.getForLogin(user);
		if (u == null) {
			return user;
		}
		return u;
	}

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
	public String login(HttpServletRequest request, String loginName, String password) {
		HttpSession session = request.getSession(false);
		ResponseMapper mapper = ResponseMapper.createMapper();
		if (session != null) {
			Map<String, Object> map = (Map<String, Object>) session.getAttribute(request.getHeader("token"));
			session.setMaxInactiveInterval(60 * 60 * 3);
			// while (session.getAttributeNames().hasMoreElements()) {
			// System.out.println(session.getAttributeNames().nextElement());
			// }
			if (map != null)
				return mapper.setData(map).getResultJson();
		}
		if (StringUtils.isBlank(loginName) || StringUtils.isBlank(password)) {
			mapper.setCode(ResultConstant.ARGS_ERROR).setMessage("姓名和密码不能为空");
			return mapper.getResultJson();
		}

		User user = new User();
		user.setLoginName(loginName.trim());
		user = this.getForLogin(user);
		if (StringUtils.isBlank(user.getId()) || !Md5Utils.MD5(password.trim()).equalsIgnoreCase(user.getPassword())) {
			mapper.setCode(ResultConstant.ARGS_ERROR).setMessage("用户名或密码不正确");
			return mapper.getResultJson();
		}

		user.setPassword(null);
		// 登录名存入session
		HttpSession session1 = request.getSession(true);
		session1.setMaxInactiveInterval(60 * 60 * 3);
		// 用户id和密码和当前时间生成的md5用于token
		String token = Md5Utils.MD5(user.getId() + password + new Date().getTime());
		session1.setAttribute(token, user);
		// 不管怎么设置过期时间 都没用 暂不知道为撒
		// session1.setMaxInactiveInterval(2060);
		Map<String, Object> result = this.user2Map(user);
		result.put("token", token);
		return mapper.setData(result).getResultJson();
	}

	@Override
	public String loginRecord(HttpServletRequest request, String userId, String device) {
		String loginIp = request.getRemoteHost();
		LoginRecord record = new LoginRecord();
		record.setUserId(userId);
		record.setLoginIp(loginIp);
		record.setDevice(device);
		record.setId(IdGenerator.uuid());
		this.userRecordDao.insert(record);
		return null;
	}

	@Override
	public String userDetail(HttpServletRequest request, String userId) {
		ResponseMapper mapper = ResponseMapper.createMapper();
		User user = new User();
		user.setId(userId);
		User u = this.userDao.get(user);
		if (u == null) {
			return mapper.setCode(ResultConstant.NOT_DATA).getResultJson();
		}

		return mapper.setData(this.user2Map(u)).getResultJson();
	}

	@Autowired
	private UserAttrDao userAttrDao;

	@Override
	public String register(HttpServletRequest request, String loginName, String password) {
		ResponseMapper mapper = ResponseMapper.createMapper();
		User user = new User();
		user.setLoginName(loginName.trim()).setPassword(Md5Utils.MD5(password.trim())).setNickname(loginName.trim());

		if (this.userDao.isExist(user) > 0) {
			return mapper.setCode(ResultConstant.EXISTS).setMessage("此账号早已注册").getResultJson();
		}
		user.setId(IdGenerator.uuid());
		try {
			if (this.userDao.insert(user) > 0) {
				UserAttr attr = new UserAttr();
				attr.setUserId(user.getId());
				this.userAttrDao.insert(attr);
				return mapper.getResultJson();
			}
		} catch (Exception e) {
			throw e;
		}

		return mapper.setCode(ResultConstant.EXCEPTION).setMessage("抱歉,注册没成功").getResultJson();
	}

	@Override
	public String editUser(HttpServletRequest request, String userId, String content, Integer flag) {
		ResponseMapper mapper = ResponseMapper.createMapper();
		User u = this.checkLoginDead(request);
		if (u == null) {
			return mapper.setCode(ResultConstant.LOGIN_INVALIDATE).setMessage("未登录或登录失效,请重新登录").getResultJson();
		}
		User temp = new User();
		temp.setId(userId);
		switch (flag) {
		case 0:// 修改头像
			if (StringUtils.isBlank(content)) {
				return mapper.setCode(ResultConstant.ARGS_ERROR).setMessage("请上传头像").getResultJson();
			}
			break;
		case 1:// 修改签名
			if (StringUtils.isNotBlank(content) && content.length() > 15) {
				return mapper.setCode(ResultConstant.ARGS_ERROR).setMessage("签名最多15个字").getResultJson();
			}
			temp.setSignature(content);
			break;
		case 2:// 修改简介
			if (StringUtils.isNotBlank(content) && content.length() > 100) {
				return mapper.setCode(ResultConstant.ARGS_ERROR).setMessage("简介最多100个字").getResultJson();
			}
			temp.setDescription(content);
			break;
		case 3:// 修改昵称
			if (StringUtils.isBlank(content)) {
				return mapper.setCode(ResultConstant.ARGS_ERROR).setMessage("昵称不能为空").getResultJson();
			}
			if (content.length() > 10) {
				return mapper.setCode(ResultConstant.ARGS_ERROR).setMessage("昵称最多10个字").getResultJson();
			}
			temp.setNickname(content);
			break;

		}
		if (this.userDao.update(temp) > 0) {
			return mapper.setCode(ResultConstant.SUCCESS).setMessage("资料修改成功").getResultJson();
		}
		return mapper.setCode(ResultConstant.EXCEPTION).setMessage("资料修改失败").getResultJson();

	}

}
