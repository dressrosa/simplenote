/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */ 
package com.xiaoyu.test.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

}