/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */ 
package com.xiaoyu.modules.biz.user.controller;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.xiaoyu.modules.biz.user.entity.User;
import com.xiaoyu.modules.biz.user.service.UserService;

/**提供给app
 * @author xiaoyu
 *2016年3月16日
 */
@RestController
@EnableAutoConfiguration
@RequestMapping(value="app/user")
public class UserController{
	
	@Autowired
	private  UserService userService;
	
	/**获取用户个人信息
	 *@author xiaoyu
	 *@param user
	 *@param request
	 *@param response
	 *@return
	 *@time 2016年3月23日下午2:33:43
	 */
	@RequestMapping(value="get",method=RequestMethod.GET)
	public Object get(@ModelAttribute User user,HttpServletRequest request,HttpServletResponse response) {
		User u = this.userService.get(user);
//		String s = JSON.toJSONString(u);
//		return s;
		return u;
	}
	
	/**查找全部用户
	 *@author xiaoyu
	 *@return
	 *@time 2016年4月1日下午7:22:58
	 */
	@RequestMapping(value="getAll",method=RequestMethod.GET)
	public Object getAll(HttpServletRequest request,HttpServletResponse response) {
		List<User> total = this.userService.findByList(new User());
		
		Iterator<String> iter = response.getHeaderNames().iterator();
		while(iter.hasNext()) {
			String s = iter.next();
			System.out.println("header:"+s+" <>:"+response.getHeader(s));
		}
//			Enumeration<String> collection = request.getAttributeNames();
//				while(collection.hasMoreElements()) {
//					System.out.println(request.getAttribute(collection.nextElement()));
//				}
		 response.setHeader("X-Content-Type-Options", "text/html");//解决nosniff 的浏览器限制安全问题
		 response.setContentType("text/plain;charset=UTF-8");
	
		return total;
	}
	
	@RequestMapping(value="/upload",method=RequestMethod.POST)
	public String upload(@ModelAttribute User user,HttpServletRequest request,HttpServletResponse response) {
		
		int temp =  this.userService.uploadImg(user);
		if(temp > 0)
			return "success";
		return "failed";
		
	}
	public static void main(String args[]) {
		SpringApplication.run(UserController.class);
	}
	
}