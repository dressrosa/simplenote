/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */ 
package com.xiaoyu.test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.xiaoyu.modules.biz.user.service.UserService;

/**
 * @author xiaoyu
 *2016年3月16日
 */
@RestController
@EnableAutoConfiguration
@RequestMapping("test")
public class TestController {
	
	@Autowired
	private UserService userService;
}