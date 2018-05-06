package com.xiaoyu.modules.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.xiaoyu.common.utils.StringUtil;
import com.xiaoyu.modules.constant.JumpPath;

/**
 * 2017年5月24日上午9:59:18
 * 
 * @author xiaoyu
 * @description 只负责地址跳转
 */
@Controller
public class RouteController {

    private String path(HttpServletRequest request, String path) {
        if (StringUtil.isPC(request)) {
            return path;
        }
        return JumpPath.MOBILE.concat(path);
    }
    
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(HttpServletRequest request, HttpServletResponse response) {
        return this.path(request, JumpPath.ArticleRelated.HOME);
    }

    @RequestMapping(value = "home", method = RequestMethod.GET)
    public String hotList(HttpServletRequest request, HttpServletResponse response) {
        return this.path(request, JumpPath.ArticleRelated.HOME);
    }

    @RequestMapping(value = "article/{articleId}", method = RequestMethod.GET)
    public String goArticleDetail(HttpServletRequest request, HttpServletResponse response,
            @PathVariable String articleId) {
        if (StringUtil.isEmpty(articleId)) {
            return this.path(request, JumpPath.CommonRelated.PAGE_404);
        }
        return this.path(request, JumpPath.ArticleRelated.DETAIL);
    }

    @RequestMapping(value = "article/write", method = RequestMethod.GET)
    public String goWrite(HttpServletRequest request) {
        return this.path(request, JumpPath.ArticleRelated.WRITE);
    }

    @RequestMapping(value = "article/edit/{articleId}", method = RequestMethod.GET)
    public String goArticleEdit(HttpServletRequest request, HttpServletResponse response,
            @PathVariable String articleId) {
        if (StringUtil.isEmpty(articleId)) {
            return this.path(request, JumpPath.CommonRelated.PAGE_404);
        }
        return this.path(request, JumpPath.ArticleRelated.EDIT);
    }

    @RequestMapping(value = "article/{articleId}/comments", method = RequestMethod.GET)
    public String goComments(HttpServletRequest request, @PathVariable String articleId) {
        return this.path(request, JumpPath.ArticleRelated.COMMENT_LIST);
    }

    @RequestMapping(value = "search", method = RequestMethod.GET)
    public String goSearch(HttpServletRequest request, String keyword) {
        return this.path(request, JumpPath.ArticleRelated.SEARCH);
    }

    @RequestMapping(value = "user/{userId}", method = RequestMethod.GET)
    public String goUserDetail(@PathVariable String userId, HttpServletRequest request) {
        if (StringUtil.isEmpty(userId)) {
            return this.path(request, JumpPath.CommonRelated.PAGE_404);
        }
        return this.path(request, JumpPath.UserRelated.DETAIL);
    }

    @RequestMapping(value = "message", method = RequestMethod.GET)
    public String goMsg(HttpServletRequest request) {
        return this.path(request, JumpPath.UserRelated.MESSAGE);
    }

    @RequestMapping(value = "user/edit", method = RequestMethod.GET)
    public String goEditUserInfo(HttpServletRequest request) {
        return this.path(request, JumpPath.UserRelated.EDIT);
    }

    @RequestMapping(value = "login", method = RequestMethod.GET)
    public String goLogin(HttpServletRequest request) {
        return this.path(request, JumpPath.CommonRelated.LOGIN);
    }

    @RequestMapping(value = "user/upload", method = RequestMethod.GET)
    public String goUpload(HttpServletRequest request, HttpServletResponse response) {
        return this.path(request, JumpPath.CommonRelated.UPLOAD);
    }

}
