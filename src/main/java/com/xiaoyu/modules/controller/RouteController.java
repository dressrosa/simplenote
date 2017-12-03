package com.xiaoyu.modules.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.xiaoyu.common.utils.StringUtil;

/**
 * 2017年5月24日上午9:59:18
 * 
 * @author xiaoyu
 * @description 只负责地址跳转
 */
@Controller
public class RouteController {

    @RequestMapping(value = "article/hot", method = RequestMethod.GET)
    public String hotList(HttpServletRequest request, HttpServletResponse response) {
        return "article/hotList";
    }

    @RequestMapping(value = "article/{articleId}", method = RequestMethod.GET)
    public String goArticleDetail(HttpServletRequest request, HttpServletResponse response,
            @PathVariable String articleId) {
        if (StringUtil.isBlank(articleId)) {
            return "common/404";
        }
        return "article/articleDetail";
    }

    @RequestMapping(value = "user/{userId}", method = RequestMethod.GET)
    public String goUserDetail(@PathVariable String userId, HttpServletRequest request) {
        if (StringUtil.isBlank(userId)) {
            return "common/404";
        }
        return "user/userDetail";
    }

    @RequestMapping(value = "article/write", method = RequestMethod.GET)
    public String goWrite(HttpServletRequest request) {
        return "article/articleForm";
    }

    @RequestMapping(value = "login", method = RequestMethod.GET)
    public String goLogin(HttpServletRequest request) {
        return "common/login";
    }

    @RequestMapping(value = "article/{articleId}/comments", method = RequestMethod.GET)
    public String goComments(HttpServletRequest request, @PathVariable String articleId) {
        return "article/commentList";
    }

    @RequestMapping(value = "user/edit", method = RequestMethod.GET)
    public String goEditUserInfo(HttpServletRequest request) {
        return "user/userForm";
    }

    /**
     * 跳转上传页面
     * 
     * @author xiaoyu
     * @param request
     * @param response
     * @return
     * @time 2016年3月29日下午4:02:17
     */
    @RequestMapping("user/upload")
    public String goUpload(HttpServletRequest request, HttpServletResponse response) {
        return "common/uploadFile";
    }

    @RequestMapping(value = "search", method = RequestMethod.GET)
    public String goSearch(HttpServletRequest request, String keyword) {
        return "article/searchList";
    }

    // 消息
    @RequestMapping(value = "message", method = RequestMethod.GET)
    public String goMsg(HttpServletRequest request) {
        return "user/messageList";
    }
}
