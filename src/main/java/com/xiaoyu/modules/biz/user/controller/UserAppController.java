/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */ 
package com.xiaoyu.modules.biz.user.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.websocket.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.xiaoyu.common.utils.StringUtils;
import com.xiaoyu.modules.biz.user.UserConstant;
import com.xiaoyu.modules.biz.user.entity.User;
import com.xiaoyu.modules.biz.user.service.UserService;

/**
 * @author xiaoyu
 *2016年4月9日
 */
@Controller
@RequestMapping("app/user")
public class UserAppController {

	@Autowired
	private UserService userService;
	
	
	@RequestMapping(value="loginWeb",method=RequestMethod.POST)
	@ResponseBody
	public Object loginWeb(HttpServletRequest request, HttpServletResponse response
			,String loginName,String password,Model model) {
		User user = new User();
		user.setLoginName(loginName);
		user = this.userService.getForLogin(user);
		Map<String,Object> map =Maps.newHashMap();
		if(StringUtils.isBlank(user.getId())) {
			map.put("message", UserConstant.NoUser.toString());
			map.put("code", 1001);
			return map;
		}
		if(!password.equalsIgnoreCase(user.getPassword())) {
			map.put("message", UserConstant.WRONGPWD.toString());
			map.put("code", 1001);
			return map;
		}
		user.setPassword(null);
		map.put("message", "success");
		map.put("code", 1000);
		map.put("user", user);
//		HttpSession session = request.getSession(true);
//		session.setAttribute("user", user);
//		session.setMaxInactiveInterval(60);
		return map;
	}
}
