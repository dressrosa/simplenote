/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.sys.controller;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.Page;
import com.xiaoyu.common.base.ResponseMapper;
import com.xiaoyu.common.base.ResultConstant;
import com.xiaoyu.common.utils.Md5Utils;
import com.xiaoyu.common.utils.StringUtils;
import com.xiaoyu.modules.biz.user.entity.User;
import com.xiaoyu.modules.biz.user.entity.UserRecord;
import com.xiaoyu.modules.biz.user.service.UserService;
import com.xiaoyu.modules.sys.constant.PageUrl;

/**
 * 用于后台页面
 * 
 * @author xiaoyu 2016年3月23日
 */
@Controller
public class UserBackController {

	@Autowired
	private UserService userService;

	/**
	 * 查看详情
	 * 
	 * @author xiaoyu
	 */
	@RequestMapping(value = "public/user/{userId}", method = RequestMethod.GET)
	public String get(@PathVariable String userId, Model model, HttpServletRequest request,
			HttpServletResponse response) {
		User user = new User();
		user.setId(userId);
		User u = this.userService.get(user);
		if (u == null) {
			return "common/404";
		}
		model.addAttribute(u);
		return "user/userDetail";
	}

	@RequestMapping(value = "private/user/modify/{userId}", method = RequestMethod.GET)
	public String detail(@PathVariable String userId, HttpServletRequest request) {
		ResponseMapper mapper = ResponseMapper.createMapper();
		if(StringUtils.isBlank(userId))
			return PageUrl.Not_Found;
		return mapper.setData(this.userService.get(userId)).getResultJson();
	}

	/**
	 * 保存信息
	 * 
	 * @author xiaoyu
	 */
	@RequestMapping(value = "private/user/save", method = RequestMethod.POST)
	@ResponseBody
	public String save(@ModelAttribute User user, Model model) {
		int total = this.userService.save(user);
		if (total > 0) {
			return "success";
		}
		return "failed";
	}

	/**
	 * 修改信息页面
	 * 
	 * @author xiaoyu
	 * @param user
	 * @param model
	 * @param json
	 * @return
	 * @time 2016年3月31日下午4:26:56
	 */
	@RequestMapping(value = "private/user/goUpdate", method = RequestMethod.GET)
	public String goUpdate(@ModelAttribute User user, Model model) {
		user = this.userService.get(user);
		model.addAttribute("user", user);
		return "user/userForm";
	}

	/**
	 * 更新信息
	 * 
	 * @author xiaoyu
	 */
	@RequestMapping(value = "private/user/update", method = RequestMethod.POST)
	@ResponseBody
	public String update(@ModelAttribute User user, Model model) {
		int total = this.userService.update(user);
		if (total > 0) {
			return "success";
		}
		return "failed";
	}

	/**
	 * 获取列表
	 * 
	 * @author xiaoyu
	 */
	@RequestMapping(value = "public/user/list", method = RequestMethod.GET)
	public String list(@ModelAttribute User user, Model model) {
		Page<User> page = this.userService.findByPage(user, 1, 5);
		model.addAttribute("list", page.getResult());
		return "user/userList";
	}

	private Map<String, Object> user2Map(User u) {
		Map<String, Object> map = new HashMap<>();
		map.put("id", u.getId());
		map.put("description", u.getDescription());
		map.put("img", u.getImg());
		map.put("nickName", u.getNickname());
		return map;
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
	@RequestMapping(value = "public/user/login", method = RequestMethod.POST)
	@ResponseBody
	public String login(HttpServletRequest request, HttpServletResponse response, String loginName, String password)
			throws IOException {
		HttpSession session = request.getSession(false);
		ResponseMapper mapper = ResponseMapper.createMapper();
		System.out.println("loginName:" + loginName + " password:" + password);
		if (session != null) {
			Map<String, Object> map = (Map<String, Object>) session.getAttribute(request.getHeader("token"));
			session.setMaxInactiveInterval(60 * 60 * 3);
			System.out.println("缓存时间:" + session.getMaxInactiveInterval());
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
		user.setLoginName(loginName);
		user = this.userService.getForLogin(user);
		if (StringUtils.isBlank(user.getId()) || !password.equalsIgnoreCase(user.getPassword())) {
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
		System.out.println("缓存时间:" + session1.getMaxInactiveInterval());
		Map<String, Object> result = this.user2Map(user);
		result.put("token", token);
		return mapper.setData(result).getResultJson();

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
	@RequestMapping(value = "private/user/loginRecord", method = RequestMethod.POST)
	@ResponseBody
	public void loginRecord(HttpServletRequest request, String userId) {
		String loginIp = request.getRemoteHost();
		UserRecord record = new UserRecord();
		record.setUserId(userId);
		record.setLoginIp(loginIp);
		this.userService.saveUserRecord(record);
	}

	/**
	 * 退出登陆
	 * 
	 * @author xiaoyu
	 * @param request
	 * @param response
	 * @time 2016年4月14日下午7:21:06
	 */
	@RequestMapping(value = "private/user/logout")
	public void logout(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		// String url =
		// response.encodeRedirectURL(request.getContextPath()+"/");
		// try {
		// response.sendRedirect(url);
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}
}