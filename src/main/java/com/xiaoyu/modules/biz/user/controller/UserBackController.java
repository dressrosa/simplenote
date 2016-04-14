/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */ 
package com.xiaoyu.modules.biz.user.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.github.pagehelper.Page;
import com.google.common.collect.Maps;
import com.xiaoyu.common.utils.StringUtils;
import com.xiaoyu.modules.biz.user.UserConstant;
import com.xiaoyu.modules.biz.user.entity.User;
import com.xiaoyu.modules.biz.user.service.UserService;
import com.xiaoyu.modules.sys.constant.StateConstant;



/**用于后台页面
 * @author xiaoyu
 *2016年3月23日
 */
@Controller
@EnableAutoConfiguration
@RequestMapping(value="back/user")
public class UserBackController{
	
	@Autowired
	private  UserService userService;
	
//	@RequestMapping(value="login",method=RequestMethod.POST)
//	public String login(@ModelAttribute User user,Model model) {
//		return "success";
//	}
	
	/**查看详情
	 *@author xiaoyu
	 */
	@RequestMapping(value="get",method=RequestMethod.GET)
	public String get(@ModelAttribute User user,Model model,
			HttpServletRequest request,HttpServletResponse response) {
		User u = this.userService.get(user);
		model.addAttribute(u);
		return "back/user/userDetail";
	}
	
	
	
	/**保存信息
	 *@author xiaoyu
	 */
	@RequestMapping(value="save",method=RequestMethod.POST)
	@ResponseBody
	public String save(@ModelAttribute User user, Model model) {
		int total = this.userService.save(user);
		if(total > 0) {
			return "success";
		}
		return "failed";
	}
	
	/**修改信息页面
	 *@author xiaoyu
	 *@param user
	 *@param model
	 *@param json
	 *@return
	 *@time 2016年3月31日下午4:26:56
	 */
	@RequestMapping(value="goUpdate",method=RequestMethod.GET)
	public String goUpdate(@ModelAttribute User user, Model model) {
		user = this.userService.get(user);
		model.addAttribute("user", user);
		return "back/user/userForm";
	}
	/**更新信息
	 *@author xiaoyu
	 */
	@RequestMapping(value="update",method=RequestMethod.POST)
	@ResponseBody
	public String update(@ModelAttribute User user, Model model) {
		int total = this.userService.update(user);
		if(total > 0) {
			return "success";
		}
		return "failed";
	}
	/**获取列表
	 *@author xiaoyu
	 */
	@RequestMapping(value="list",method=RequestMethod.GET)
	public String list(@ModelAttribute User user, Model model) {
		Page<User> page = this.userService.findByPage(user, 1, 5);
		model.addAttribute("list", page.getResult());
		return "back/user/userList";
	}
	
	
	/**正常登录
	 *@author xiaoyu
	 *@param request
	 *@param response
	 *@param loginName
	 *@param password
	 *@return
	 *@time 2016年4月14日下午8:24:06
	 */
	@RequestMapping(value="login",method=RequestMethod.POST)
	public String login(HttpServletRequest request, HttpServletResponse response
			,String loginName,String password,Model model) {
	
		HttpSession session = request.getSession(false);
		if(session != null) {
			
		}
		else {
			response.encodeRedirectURL("/html/app/webLogin.html");
		}
		if(StringUtils.isBlank(loginName)||StringUtils.isBlank(password)) {
			model.addAttribute("errorMsg", "姓名和密码不能为空");
			response.encodeRedirectURL("/html/app/webLogin.html");
		}
		
		User user = new User();
		user.setLoginName(loginName);
		user = this.userService.getForLogin(user);
		if(StringUtils.isBlank(user.getId())) {
			model.addAttribute("errorMsg", UserConstant.NoUser.toString());
			response.encodeRedirectURL("/html/app/webLogin.html");
		}
		if(!password.equalsIgnoreCase(user.getPassword())) {
			model.addAttribute("message", UserConstant.WRONGPWD.toString());
			response.encodeRedirectURL("/html/app/webLogin.html");
		}
		user.setPassword(null);
		model.addAttribute("user", user);
		//登录名存入session
		HttpSession session = request.getSession(true);
		session.setAttribute("user_loginName", user.getLoginName());
		session.setMaxInactiveInterval(60);
		return "back/article/articleForm";
	}
}