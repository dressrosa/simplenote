/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */ 
package com.xiaoyu.modules.biz.user.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.xiaoyu.common.utils.StringUtils;
import com.xiaoyu.modules.biz.user.UserConstant;
import com.xiaoyu.modules.biz.user.entity.User;
import com.xiaoyu.modules.biz.user.entity.UserRecord;
import com.xiaoyu.modules.biz.user.service.UserService;
import com.xiaoyu.modules.sys.constant.StateConstant;

/**
 * @author xiaoyu
 *2016年4月9日
 */
@Controller
@RequestMapping("app/user")
public class UserAppController {

	@Autowired
	private UserService userService;
	
	
	/**ajax
	 *@author xiaoyu
	 *@param request
	 *@param response
	 *@param loginName
	 *@param password
	 *@return
	 *@time 2016年4月14日下午8:23:52
	 */
	@RequestMapping(value="loginWeb",method=RequestMethod.POST)
	@ResponseBody
	public Object loginWeb(HttpServletRequest request, HttpServletResponse response
			,String loginName,String password) {
		Map<String,Object> map =Maps.newHashMap();
		if(StringUtils.isBlank(loginName)||StringUtils.isBlank(password)) {
			map.put("code",StateConstant.FAILURE.toString());
			map.put("message","请按规则出牌,别想PASS");
			return map;
		}
		
		User user = new User();
		user.setLoginName(loginName);
		user = this.userService.getForLogin(user);
		if(StringUtils.isBlank(user.getId())) {
			map.put("message", UserConstant.NoUser.toString());
			map.put("code", StateConstant.FAILURE.toString());
			return map;
		}
		if(!password.equalsIgnoreCase(user.getPassword())) {
			map.put("code", StateConstant.FAILURE.toString());
			map.put("message", UserConstant.WRONGPWD.toString());
			return map;
		}
		user.setPassword(null);
		map.put("message", "success");
		map.put("code", StateConstant.SUCCESS.toString());
		map.put("user", user);
		//登录名存入session
		HttpSession session = request.getSession(true);
		session.setAttribute("user_loginName", user.getLoginName());
		session.setMaxInactiveInterval(60);
		return map;
	}
	
	
	
	
	/**记录ip
	 *@author xiaoyu
	 *@param request
	 *@param userId
	 *@return
	 *@time 2016年4月12日上午10:30:37
	 */
	@RequestMapping(value="loginRecord",method=RequestMethod.POST)
	public void loginRecord(HttpServletRequest request,String userId) {
		String loginIp = request.getRemoteHost();
		UserRecord record = new UserRecord();
		record.setUserId(userId);
		record.setLoginIp(loginIp);
		this.userService.saveUserRecord(record);
	}
	
	/**退出登陆
	 *@author xiaoyu
	 *@param request
	 *@param response
	 *@time 2016年4月14日下午7:21:06
	 */
	@RequestMapping(value="logout")
	public void logout(HttpServletRequest request,HttpServletResponse response) {
		HttpSession session  = request.getSession(false);
		if(session != null) {
			session.invalidate();
		}
		String url = response.encodeRedirectURL(request.getContextPath()+"/");
		try {
			response.sendRedirect(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
