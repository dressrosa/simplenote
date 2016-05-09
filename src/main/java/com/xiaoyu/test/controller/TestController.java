/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */ 
package com.xiaoyu.test.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Maps;
import com.xiaoyu.common.utils.IdGenerator;
import com.xiaoyu.modules.biz.user.entity.User;
import com.xiaoyu.modules.biz.user.service.UserService;

/**
 * @author xiaoyu
 *2016年3月16日
 */
//@RestController
@Controller
@EnableAutoConfiguration
@RequestMapping("test")
public class TestController {
	
	@Autowired
	private UserService userService;

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@RequestMapping("/")
	public String index(HttpServletRequest req,HttpServletResponse resp) {
		 String attributeName = req.getParameter("attributeName");
	        String attributeValue = req.getParameter("attributeValue");
	        req.getSession().setAttribute(attributeName, attributeValue);
//	        try {
//				//resp.sendRedirect(req.getContextPath() + "/");
//	        	resp.sendRedirect("test/hello");
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		return "test/hello";
	}
	
	/**mongo测试
	 *@author xiaoyu
	 *@param req
	 *@param resp
	 *@return
	 *@time 2016年5月9日下午3:27:29
	 */
	@RequestMapping("mongo")
	@ResponseBody
	public Object mongo(HttpServletRequest req,HttpServletResponse resp) {
		User user =new User();
		user.setId("123");
		user.setNickName("小明");
		user.setLoginName("xiaoyu");
		mongoTemplate.insert(user);
//		Map<String,Object> map = Maps.newHashMap();
//		map.put("id", "123");
		
		Query query = new Query(Criteria.where("id").is("123"));
		return mongoTemplate.findOne(query, User.class);
	}

}