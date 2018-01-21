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

    @RequestMapping(value = "article/hot", method = RequestMethod.GET)
    public String hotList(HttpServletRequest request, HttpServletResponse response) {
        return JumpPath.ArticleRelated.HOT_LIST;
    }

    @RequestMapping(value = "article/{articleId}", method = RequestMethod.GET)
    public String goArticleDetail(HttpServletRequest request, HttpServletResponse response,
            @PathVariable String articleId) {
        if (StringUtil.isBlank(articleId)) {
            return JumpPath.CommonRelated.PAGE_404;
        }
        return JumpPath.ArticleRelated.DETAIL;
    }

    @RequestMapping(value = "article/write", method = RequestMethod.GET)
    public String goWrite(HttpServletRequest request) {
        return JumpPath.ArticleRelated.WRITE;
    }

    @RequestMapping(value = "article/edit/{articleId}", method = RequestMethod.GET)
    public String goArticleEdit(HttpServletRequest request, HttpServletResponse response,
            @PathVariable String articleId) {
        if (StringUtil.isBlank(articleId)) {
            return JumpPath.CommonRelated.PAGE_404;
        }
        return JumpPath.ArticleRelated.EDIT;
    }

    @RequestMapping(value = "article/{articleId}/comments", method = RequestMethod.GET)
    public String goComments(HttpServletRequest request, @PathVariable String articleId) {
        return JumpPath.ArticleRelated.COMMENT_LIST;
    }

    @RequestMapping(value = "search", method = RequestMethod.GET)
    public String goSearch(HttpServletRequest request, String keyword) {
        return JumpPath.ArticleRelated.SEARCH;
    }

    @RequestMapping(value = "user/{userId}", method = RequestMethod.GET)
    public String goUserDetail(@PathVariable String userId, HttpServletRequest request) {
        if (StringUtil.isBlank(userId)) {
            return JumpPath.CommonRelated.PAGE_404;
        }
        return JumpPath.UserRelated.DETAIL;
    }

    @RequestMapping(value = "message", method = RequestMethod.GET)
    public String goMsg(HttpServletRequest request) {
        return JumpPath.UserRelated.MESSAGE;
    }

    @RequestMapping(value = "user/edit", method = RequestMethod.GET)
    public String goEditUserInfo(HttpServletRequest request) {
        return JumpPath.UserRelated.EDIT;
    }

    @RequestMapping(value = "login", method = RequestMethod.GET)
    public String goLogin(HttpServletRequest request) {
        return JumpPath.CommonRelated.LOGIN;
    }

    @RequestMapping(value = "user/upload", method = RequestMethod.GET)
    public String goUpload(HttpServletRequest request, HttpServletResponse response) {
        return JumpPath.CommonRelated.UPLOAD;
    }

}
