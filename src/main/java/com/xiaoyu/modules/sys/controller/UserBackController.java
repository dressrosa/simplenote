/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.sys.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.Page;
import com.xiaoyu.common.base.ResponseMapper;
import com.xiaoyu.common.base.ResultConstant;
import com.xiaoyu.common.utils.StringUtils;
import com.xiaoyu.modules.biz.user.entity.User;
import com.xiaoyu.modules.biz.user.entity.UserRecord;
import com.xiaoyu.modules.biz.user.service.IUserService;
import com.xiaoyu.modules.sys.constant.PageUrl;

/**
 * 用于后台页面
 * 
 * @author xiaoyu 2016年3月23日
 */
@RestController
public class UserBackController {

	@Autowired
	private IUserService userService;

	/**
	 * 查看详情
	 * 
	 * @author xiaoyu
	 */
	@RequestMapping(value = "api/v1/user/{userId}", method = RequestMethod.GET)
	public String userDetail(@PathVariable String userId, HttpServletRequest request) {
		if (StringUtils.isBlank(userId)) {
			return ResponseMapper.createMapper().setCode(ResultConstant.ARGS_ERROR).getResultJson();
		}
		return this.userService.userDetail(request, userId);
	}

	/**
	 * 正常登录
	 * 
	 * @author xiaoyu
	 * @param request
	 * @param response
	 * @param loginName
	 * @param password
	 * @return
	 * @throws IOException
	 * @time 2016年4月14日下午8:24:06
	 */
	@RequestMapping(value = "api/v1/user/login", method = RequestMethod.POST)
	@ResponseBody
	public String login(HttpServletRequest request, String loginName, String password) throws IOException {
		if (StringUtils.isAnyBlank(loginName, password)) {
			ResponseMapper mapper = ResponseMapper.createMapper();
			return mapper.setCode(ResultConstant.ARGS_ERROR).getResultJson();
		}
		return this.userService.login(request, loginName, password);

	}

	/**
	 * 记录ip
	 * 
	 * @author xiaoyu
	 * @param request
	 * @param userId
	 * @return
	 * @time 2016年4月12日上午10:30:37
	 */
	@RequestMapping(value = "api/v1/user/loginRecord", method = RequestMethod.POST)
	public String loginRecord(HttpServletRequest request, String userId) {
		if (StringUtils.isNotBlank(userId))
			return this.userService.loginRecord(request, userId);
		return null;
	}

	/**
	 * 退出登陆
	 * 
	 * @author xiaoyu
	 * @param request
	 * @param response
	 * @time 2016年4月14日下午7:21:06
	 */
	@RequestMapping(value = "api/v1/user/logout")
	public void logout(HttpServletRequest request, String token) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			if (session.getAttribute(token) != null) {
				session.removeAttribute(token);
			}
			// session.invalidate();
		}
	}
}