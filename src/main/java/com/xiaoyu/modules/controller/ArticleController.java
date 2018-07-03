/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xiaoyu.common.base.ResponseCode;
import com.xiaoyu.common.base.ResponseMapper;
import com.xiaoyu.common.utils.StringUtil;
import com.xiaoyu.modules.biz.article.service.api.IArticleService;

/**
 * 文章
 * 
 * @author xiaoyu 2016年3月29日
 */
@RestController
public class ArticleController {

    @Autowired
    private IArticleService articleService;

    /**
     * 获取文章详情
     * 
     * @author xiaoyu
     * @param article
     * @param model
     * @param request
     * @param response
     * @return
     * @time 2016年3月29日下午9:20:30
     */
    @RequestMapping(value = "api/v1/article/{articleId}", method = RequestMethod.GET)
    public String detail(@PathVariable String articleId, HttpServletRequest request) {
        if (StringUtil.isEmpty(articleId)) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.ARGS_ERROR.statusCode())
                    .resultJson();
        }
        return this.articleService.detail(articleId);
    }

    /**
     * 首页的列表
     * 
     * @author xiaoyu
     * @param model
     * @return
     * @time 2016年4月1日下午4:08:19
     */
    @RequestMapping(value = "api/v1/home", method = RequestMethod.GET)
    public String hotList(HttpServletRequest request) {
        return this.articleService.hotList(request);
    }

    /**
     * 文章列表
     * 
     * @author xiaoyu
     * @param article
     * @param model
     * @param pageNum
     * @param pageSize
     * @return
     * @time 2016年3月30日下午2:16:59
     */
    @RequestMapping(value = "api/v1/article/list", method = RequestMethod.GET)
    public String list(HttpServletRequest request, String userId) {
        if (StringUtil.isEmpty(userId)) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.ARGS_ERROR.statusCode())
                    .resultJson();
        }
        return this.articleService.list(request, userId);
    }

    @RequestMapping(value = "api/v1/article/latest", method = RequestMethod.POST)
    public String latestOfUsers(HttpServletRequest request, @RequestParam("userId[]") String[] userId) {
        return this.articleService.latestOfUsers(request, userId);
    }

    @RequestMapping(value = "api/v1/article/list/collect", method = RequestMethod.GET)
    public String collectList(HttpServletRequest request, String userId) {
        if (StringUtil.isEmpty(userId)) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.ARGS_ERROR.statusCode())
                    .resultJson();
        }
        return this.articleService.collectList(request, userId);
    }

    @RequestMapping(value = "api/v1/article/views/{articleId}")
    public String changeViewNum(HttpServletRequest request, @PathVariable String articleId) {
        if (StringUtil.isEmpty(articleId)) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.ARGS_ERROR.statusCode())
                    .resultJson();
        }
        return this.articleService.addReadNum(request, articleId);
    }

    @RequestMapping(value = "api/v1/article/add", method = RequestMethod.POST)
    public String addArticle(HttpServletRequest request, @RequestParam(required = true) String title,
            @RequestParam(required = true) String content) {
        return this.articleService.addArticle(request, title, content);
    }

    @RequestMapping(value = "api/v1/article/edit", method = RequestMethod.POST)
    public String editArticle(HttpServletRequest request, @RequestParam(required = true) String title,
            @RequestParam(required = true) String content, @RequestParam(required = true) String userId,
            @RequestParam(required = true) String articleId) {
        return this.articleService.editArticle(request, title, content, userId, articleId);
    }

    @RequestMapping(value = "api/v1/article/like", method = RequestMethod.POST)
    public String like(HttpServletRequest request, @RequestParam(required = true) String articleId,
            @RequestParam(required = true) Integer isLike) {
        return this.articleService.addLike(request, articleId, isLike);
    }

    @RequestMapping(value = "api/v1/article/collect", method = RequestMethod.POST)
    public String collect(HttpServletRequest request, @RequestParam(required = true) String articleId,
            @RequestParam(required = true) Integer isCollect) {
        return this.articleService.addCollect(request, articleId, isCollect);
    }

    @RequestMapping(value = "api/v1/article/{articleId}/comment", method = RequestMethod.POST)
    public String comment(HttpServletRequest request, @PathVariable String articleId,
            @RequestParam(required = true) String content) {
        return this.articleService.comment(request, articleId, content);
    }

    @RequestMapping(value = "api/v1/article/{commentId}/reply", method = RequestMethod.POST)
    public String reply(HttpServletRequest request, @PathVariable String commentId,
            @RequestParam(required = true) String replyContent) {
        return this.articleService.reply(request, commentId, replyContent);
    }

    @RequestMapping(value = "api/v1/article/{articleId}/new-comments", method = RequestMethod.GET)
    public String newComments(HttpServletRequest request, @PathVariable String articleId) {
        return this.articleService.newComments(request, articleId);
    }

    @RequestMapping(value = "api/v1/article/{articleId}/comments", method = RequestMethod.GET)
    public String comments(HttpServletRequest request, @PathVariable String articleId) {
        return this.articleService.comments(request, articleId);
    }

    @RequestMapping(value = "api/v1/article/comments/like", method = RequestMethod.POST)
    public String commentLike(HttpServletRequest request, @RequestParam(required = true) String commentId,
            @RequestParam(required = true) Integer isLike) {
        return this.articleService.addCommentLike(request, commentId, isLike);
    }

    @RequestMapping(value = "api/v1/article/search", method = RequestMethod.GET)
    public String search(HttpServletRequest request, @RequestParam(required = true) String keyword) {
        return this.articleService.search(request, keyword);
    }

    @RequestMapping(value = "api/v1/article/columns", method = RequestMethod.GET)
    public String columns(HttpServletRequest request, @RequestParam(required = true) String userId) {
        return this.articleService.columns(request, userId);
    }
}