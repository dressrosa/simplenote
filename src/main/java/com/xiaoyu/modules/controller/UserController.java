/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xiaoyu.common.base.ResponseCode;
import com.xiaoyu.common.base.ResponseMapper;
import com.xiaoyu.common.utils.StringUtil;
import com.xiaoyu.modules.biz.user.service.api.IUserService;

/**
 * 用户相关
 * 
 * @author xiaoyu 2016年3月23日
 */
@RestController
public class UserController {

    @Autowired
    private IUserService userService;

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
    public String login(HttpServletRequest request, String loginName, String password) throws IOException {
        if (StringUtil.isAnyEmpty(loginName, password)) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.ARGS_ERROR.statusCode())
                    .resultJson();
        }
        return this.userService.login(request, loginName, password);
    }

    @RequestMapping(value = "api/v1/user/register", method = RequestMethod.POST)
    public String register(HttpServletRequest request, @RequestParam(required = true) String loginName,
            @RequestParam(required = true) String password, @RequestParam(required = true) String repassword)
            throws IOException {
        final ResponseMapper mapper = ResponseMapper.createMapper();
        if (!StringUtil.isMobile(loginName) && !StringUtil.isEmail(loginName)) {
            return mapper.code(ResponseCode.ARGS_ERROR.statusCode())
                    .message("请填写正确的邮箱或手机号")
                    .resultJson();
        }
        if (password.length() < 6) {
            return mapper.code(ResponseCode.ARGS_ERROR.statusCode())
                    .message("密码长度至少6位")
                    .resultJson();
        }
        if (!password.equals(repassword)) {
            return mapper.code(ResponseCode.ARGS_ERROR.statusCode())
                    .message("密码填写不一致")
                    .resultJson();
        }
        return this.userService.register(request, loginName, password);
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
    @RequestMapping(value = "api/v1/user/login/record", method = RequestMethod.POST)
    public String loginRecord(HttpServletRequest request, String userId, String device) {
        if (StringUtil.isNotBlank(userId)) {
            return this.userService.loginRecord(request, userId, device);
        }
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
        final HttpSession session = request.getSession(false);
        if (session != null) {
            if (session.getAttribute(token) != null) {
                session.removeAttribute(token);
            }
            // session.invalidate();
        }
    }

    /**
     * 查看详情
     * 
     * @author xiaoyu
     */
    @RequestMapping(value = "api/v1/user/{userId}", method = RequestMethod.GET)
    public String userDetail(@PathVariable String userId, HttpServletRequest request) {
        if (StringUtil.isEmpty(userId)) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.ARGS_ERROR.statusCode())
                    .resultJson();
        }
        return this.userService.userDetail(request, userId);
    }

    /**
     * 编辑信息 flag 0头像 1签名 2简介 3昵称 4背景图片
     * 
     * @return
     */
    @RequestMapping(value = "api/v1/user/{userId}/edit", method = RequestMethod.POST)
    public String editInfo(HttpServletRequest request, @PathVariable String userId, String content,
            @RequestParam(required = true) Integer flag) {
        if (StringUtil.isEmpty(userId)) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.ARGS_ERROR.statusCode())
                    .resultJson();
        }
        return this.userService.editUser(request, userId, content, flag);
    }

    @RequestMapping(value = "api/v1/user/follow", method = RequestMethod.POST)
    public String followUser(HttpServletRequest request, String userId, String followTo) {
        return this.userService.followUser(request, userId, followTo);
    }

    @RequestMapping(value = "api/v1/user/unfollow", method = RequestMethod.POST)
    public String cancelFollow(HttpServletRequest request, String userId, String followTo) {
        return this.userService.cancelFollow(request, userId, followTo);
    }

    @RequestMapping(value = "api/v1/user/is-followed", method = RequestMethod.POST)
    public String isFollowed(HttpServletRequest request, String userId, String followTo) {
        return this.userService.isFollowed(request, userId, followTo);
    }

    // 追随者
    @RequestMapping(value = "api/v1/user/follower", method = RequestMethod.GET)
    public String follower(HttpServletRequest request, String userId) {
        return this.userService.follower(request, userId);
    }

    // 关注的人
    @RequestMapping(value = "api/v1/user/following", method = RequestMethod.GET)
    public String following(HttpServletRequest request, String userId) {
        return this.userService.following(request, userId);
    }

    // 获取常用的统计数
    @RequestMapping(value = "api/v1/user/commonNums", method = RequestMethod.GET)
    public String commonNums(HttpServletRequest request, String userId) {
        return this.userService.commonNums(request, userId);
    }

}