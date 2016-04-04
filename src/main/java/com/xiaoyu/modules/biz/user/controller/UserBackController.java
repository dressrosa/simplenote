/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */ 
package com.xiaoyu.modules.biz.user.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.xiaoyu.modules.biz.user.entity.User;
import com.xiaoyu.modules.biz.user.service.UserService;



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
	
	@RequestMapping(value="login",method=RequestMethod.POST)
	public String login(@ModelAttribute User user,Model model) {
		return "success";
	}
	
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
}