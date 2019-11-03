/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.simplenote.modules.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.xiaoyu.maple.core.MapleUtil;
import com.xiaoyu.simplenote.common.base.ResponseCode;
import com.xiaoyu.simplenote.common.base.ResponseMapper;
import com.xiaoyu.simplenote.common.request.TraceRequest;
import com.xiaoyu.simplenote.common.util.ImgUtils;
import com.xiaoyu.simplenote.common.util.Utils;
import com.xiaoyu.simplenote.common.utils.Md5Utils;
import com.xiaoyu.simplenote.common.utils.StringUtil;
import com.xiaoyu.simplenote.modules.biz.article.service.api.IArticleService;
import com.xiaoyu.simplenote.modules.biz.user.entity.User;
import com.xiaoyu.simplenote.modules.biz.user.service.api.IUserService;

/**
 * 用户相关
 * 
 * @author xiaoyu 2016年3月23日
 */
@RestController
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IArticleService articleService;

    @Autowired
    private ImgUtils imgUtils;

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
    public String login(HttpServletRequest request, @RequestBody JSONObject json) throws IOException {
        String loginName = json.getString("loginName");
        String password = json.getString("password");
        if (StringUtil.isAnyEmpty(loginName, password)) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.ARGS_ERROR.statusCode())
                    .resultJson();
        }
        ResponseMapper mapper = ResponseMapper.createMapper();
        HttpSession session = request.getSession(false);
        if (session != null) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) session.getAttribute(request.getHeader("token"));
            // 4hours
            session.setMaxInactiveInterval(3600 << 2);
            if (map != null) {
                return mapper.data(map).resultJson();
            }
        }
        User user = this.userService.login(loginName, password);
        if (user == null) {
            return mapper.code(ResponseCode.ARGS_ERROR.statusCode())
                    .message("用户名或密码不正确")
                    .resultJson();
        }
        // 消去密码
        user.setPassword(null);
        // 登录名存入session
        HttpSession tsession = request.getSession(true);
        // 7d
        tsession.setMaxInactiveInterval(604000);
        // 用户id和密码和当前时间生成的md5用于token
        String token = Md5Utils.md5(user.getId() + password + System.currentTimeMillis());
        tsession.setAttribute(token, user);
        // 不管怎么设置过期时间 都没用 暂不知道为撒
        // tsession.setMaxInactiveInterval(2060);
        Map<String, Object> result = MapleUtil.wrap(user)
                .rename("uuid", "userId")
                .skip("password")
                .skip("sex")
                .skip("loginName")
                .map();
        result.put("token", token);
        return mapper.data(result).resultJson();
    }

    @RequestMapping(value = "api/v1/user/loginInfo", method = RequestMethod.GET)
    public String loginInfo(HttpServletRequest request) {
        TraceRequest req = Utils.getTraceRequest(request);
        return ResponseMapper.createMapper().data(req.isLogin()).resultJson();
    }

    @RequestMapping(value = "api/v1/user/register", method = RequestMethod.POST)
    public String register(HttpServletRequest request, @RequestBody JSONObject json)
            throws IOException {
        String loginName = json.getString("loginName");
        String password = json.getString("password");
        String repassword = json.getString("repassword");
        if (StringUtil.isAnyEmpty(loginName, password, repassword)) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.ARGS_ERROR.statusCode())
                    .resultJson();
        }
        ResponseMapper mapper = ResponseMapper.createMapper();
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
        TraceRequest req = Utils.getTraceRequest(request);
        return this.userService.register(req, loginName, password).resultJson();
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
    public String loginRecord(HttpServletRequest request, @RequestBody JSONObject json) {
        String userId = json.getString("userId");
        String device = json.getString("device");
        if (StringUtil.isNotBlank(userId)) {
            TraceRequest req = Utils.getTraceRequest(request);
            return this.userService.loginRecord(req, userId, device).resultJson();
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
    public void logout(HttpServletRequest request, @RequestBody JSONObject json) {
        String token = json.getString("token");
        HttpSession session = request.getSession(false);
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
    public String userDetail(HttpServletRequest request, @PathVariable String userId) {
        if (StringUtil.isEmpty(userId)) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.ARGS_ERROR.statusCode())
                    .resultJson();
        }
        TraceRequest req = Utils.getTraceRequest(request);
        return this.userService.userDetail(req, userId).resultJson();
    }

    /**
     * 编辑信息 flag 0头像 1签名 2简介 3昵称 4背景图片
     * 
     * @return
     */
    @RequestMapping(value = "api/v1/user/edit", method = RequestMethod.POST)
    public String editInfo(HttpServletRequest request, @RequestBody JSONObject json) {
        TraceRequest req = Utils.getTraceRequest(request);
        if (!req.isLogin()) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.LOGIN_INVALIDATE.statusCode())
                    .message("未登录或登录失效,请重新登录").resultJson();
        }
        String content = json.getString("content");
        Integer flag = json.getInteger("flag");
        if (StringUtil.isBlank(content)) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.ARGS_ERROR.statusCode())
                    .resultJson();
        }
        // 头像/背景图
        if (flag == 0 || flag == 4) {
            if (content.startsWith("http")) {
                // do nothing
            } else if (content.startsWith("data:image/jpeg;base64,")) {
                content = content.substring(23);
                content = imgUtils.saveImgToTencentOss(Base64.decodeBase64(content));
            } else if (content.startsWith("data:image/png;base64,")) {
                content = content.substring(22);
                content = imgUtils.saveImgToTencentOss(Base64.decodeBase64(content));
            } else {
                return ResponseMapper.createMapper()
                        .code(ResponseCode.ARGS_ERROR.statusCode())
                        .message("图片格式不正确").resultJson();
            }

        }
        return this.userService.editUser(req, content, flag).resultJson();
    }

    @RequestMapping(value = "api/v1/user/follow", method = RequestMethod.POST)
    public String followUser(HttpServletRequest request, @RequestBody JSONObject json) {
        String userId = json.getString("userId");
        String followTo = json.getString("followTo");
        if (StringUtil.isAnyEmpty(userId, followTo)) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.ARGS_ERROR.statusCode())
                    .resultJson();
        }
        TraceRequest req = Utils.getTraceRequest(request);
        if (!req.isLogin()) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.LOGIN_INVALIDATE.statusCode())
                    .resultJson();
        }
        return this.userService.followUser(req, userId, followTo).resultJson();
    }

    @RequestMapping(value = "api/v1/user/unfollow", method = RequestMethod.POST)
    public String cancelFollow(HttpServletRequest request, @RequestBody JSONObject json) {
        String userId = json.getString("userId");
        String followTo = json.getString("followTo");
        if (StringUtil.isAnyEmpty(userId, followTo)) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.ARGS_ERROR.statusCode())
                    .resultJson();
        }
        TraceRequest req = Utils.getTraceRequest(request);
        if (!req.isLogin()) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.LOGIN_INVALIDATE.statusCode())
                    .resultJson();
        }
        return this.userService.cancelFollow(req, userId, followTo).resultJson();
    }

    @RequestMapping(value = "api/v1/user/is-followed", method = RequestMethod.POST)
    public String isFollowed(HttpServletRequest request, @RequestBody JSONObject json) {
        String userId = json.getString("userId");
        String followTo = json.getString("followTo");
        if (StringUtil.isAnyEmpty(userId, followTo)) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.ARGS_ERROR.statusCode())
                    .resultJson();
        }
        TraceRequest req = Utils.getTraceRequest(request);
        if (!req.isLogin()) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.LOGIN_INVALIDATE.statusCode())
                    .resultJson();
        }
        return this.userService.isFollowed(req, userId, followTo).resultJson();
    }

    // 追随者
    @RequestMapping(value = "api/v1/user/follower", method = RequestMethod.GET)
    public String follower(HttpServletRequest request, String userId) {
        TraceRequest req = Utils.getTraceRequest(request);
        if (!req.isLogin()) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.LOGIN_INVALIDATE.statusCode())
                    .resultJson();
        }
        return this.userService.follower(req, userId).resultJson();
    }

    // 关注的人
    @RequestMapping(value = "api/v1/user/following", method = RequestMethod.GET)
    public String following(HttpServletRequest request, String userId) {
        TraceRequest req = Utils.getTraceRequest(request);
        if (!req.isLogin()) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.LOGIN_INVALIDATE.statusCode())
                    .resultJson();
        }
        return this.userService.following(req, userId).resultJson();
    }

    // 获取常用的统计数
    @RequestMapping(value = "api/v1/user/commonNums", method = RequestMethod.GET)
    public String commonNums(HttpServletRequest request, String userId) {
        TraceRequest req = Utils.getTraceRequest(request);
        if (!req.isLogin()) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.LOGIN_INVALIDATE.statusCode())
                    .resultJson();
        }
        return this.userService.commonNums(req, userId).resultJson();
    }

    @RequestMapping(value = "api/v1/user/comments", method = RequestMethod.GET)
    public String userComments(HttpServletRequest request) {
        TraceRequest req = Utils.getTraceRequest(request);
        if (!req.isLogin()) {
            return ResponseMapper.createMapper().code(ResponseCode.LOGIN_INVALIDATE.statusCode())
                    .resultJson();
        }
        return this.articleService.userComments(req, req.getUser().getUuid()).resultJson();
    }
}