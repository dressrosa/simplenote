/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.xiaoyu.common.base.ResponseCode;
import com.xiaoyu.common.base.ResponseMapper;
import com.xiaoyu.common.request.TraceRequest;
import com.xiaoyu.common.util.Utils;
import com.xiaoyu.common.utils.StringUtil;
import com.xiaoyu.modules.biz.article.service.api.IArticleService;
import com.xiaoyu.modules.biz.article.vo.ArticleColumnVo;

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
    public String detail(HttpServletRequest request, @PathVariable String articleId) {
        if (StringUtil.isEmpty(articleId)) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.ARGS_ERROR.statusCode())
                    .resultJson();
        }
        return this.articleService.detail(articleId).resultJson();
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
        TraceRequest req = Utils.getTraceRequest(request);
        return this.articleService.hotList(req).resultJson();
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
        TraceRequest req = Utils.getTraceRequest(request);
        return this.articleService.list(req, userId).resultJson();
    }

    @RequestMapping(value = "api/v1/article/latest", method = RequestMethod.GET)
    public String latestOfUsers(HttpServletRequest request, @RequestParam("userId[]") String[] userId) {
        TraceRequest req = Utils.getTraceRequest(request);
        return this.articleService.latestOfUsers(req, userId).resultJson();
    }

    @RequestMapping(value = "api/v1/article/collects", method = RequestMethod.GET)
    public String collectList(HttpServletRequest request, String userId) {
        TraceRequest req = Utils.getTraceRequest(request);
        return this.articleService.collectList(req, userId).resultJson();
    }

    @RequestMapping(value = "api/v1/article/views/{articleId}")
    public String changeViewNum(HttpServletRequest request, @PathVariable String articleId) {
        if (StringUtil.isEmpty(articleId)) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.ARGS_ERROR.statusCode())
                    .resultJson();
        }
        TraceRequest req = Utils.getTraceRequest(request);
        return this.articleService.addReadNum(req, articleId).resultJson();
    }

    @RequestMapping(value = "api/v1/article/add", method = RequestMethod.POST)
    public String addArticle(HttpServletRequest request, @RequestBody JSONObject json) {
        if (!json.containsKey("title") || !json.containsKey("content")) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.ARGS_ERROR.statusCode()).resultJson();
        }
        TraceRequest req = Utils.getTraceRequest(request);
        return this.articleService.addArticle(req, json.getString("title"), json.getString("content"))
                .resultJson();
    }

    @RequestMapping(value = "api/v1/article/edit", method = RequestMethod.POST)
    public String editArticle(HttpServletRequest request, @RequestBody JSONObject json) {
        if (!json.containsKey("title") || !json.containsKey("content") || !json.containsKey("userId")
                || !json.containsKey("articleId")) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.ARGS_ERROR.statusCode()).resultJson();
        }
        TraceRequest req = Utils.getTraceRequest(request);
        return this.articleService.editArticle(req, json.getString("title"), json.getString("content"),
                json.getString("userId"), json.getString("articleId")).resultJson();
    }

    @RequestMapping(value = "api/v1/article/like", method = RequestMethod.POST)
    public String like(HttpServletRequest request, @RequestBody JSONObject json) {
        if (!json.containsKey("articleId") || !json.containsKey("isLike")) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.ARGS_ERROR.statusCode()).resultJson();
        }
        TraceRequest req = Utils.getTraceRequest(request);
        return this.articleService.addLike(req, json.getString("articleId"), json.getInteger("isLike")).resultJson();
    }

    @RequestMapping(value = "api/v1/article/collect", method = RequestMethod.POST)
    public String collect(HttpServletRequest request, @RequestBody JSONObject json) {
        if (!json.containsKey("articleId") || !json.containsKey("isCollect")) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.ARGS_ERROR.statusCode()).resultJson();
        }
        TraceRequest req = Utils.getTraceRequest(request);
        return this.articleService
                .addCollect(req, json.getString("articleId"), json.getInteger("isCollect")).resultJson();
    }

    @RequestMapping(value = "api/v1/article/{articleId}/comment", method = RequestMethod.POST)
    public String comment(HttpServletRequest request, @PathVariable String articleId,
            @RequestBody JSONObject json) {
        if (!json.containsKey("content")) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.ARGS_ERROR.statusCode()).resultJson();
        }
        TraceRequest req = Utils.getTraceRequest(request);
        return this.articleService.comment(req, articleId, json.getString("content")).resultJson();
    }

    @RequestMapping(value = "api/v1/article/{commentId}/reply", method = RequestMethod.POST)
    public String reply(HttpServletRequest request, @PathVariable String commentId, @RequestBody JSONObject json) {
        if (!json.containsKey("replyContent")) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.ARGS_ERROR.statusCode()).resultJson();
        }
        TraceRequest req = Utils.getTraceRequest(request);
        return this.articleService.reply(req, commentId, json.getString("replyContent")).resultJson();
    }

    @RequestMapping(value = "api/v1/article/{articleId}/new-comments", method = RequestMethod.GET)
    public String newComments(HttpServletRequest request, @PathVariable String articleId) {
        TraceRequest req = Utils.getTraceRequest(request);
        return this.articleService.newComments(req, articleId).resultJson();
    }

    @RequestMapping(value = "api/v1/article/{articleId}/comments", method = RequestMethod.GET)
    public String comments(HttpServletRequest request, @PathVariable String articleId) {
        TraceRequest req = Utils.getTraceRequest(request);
        return this.articleService.comments(req, articleId).resultJson();
    }

    @RequestMapping(value = "api/v1/article/comments/like", method = RequestMethod.POST)
    public String commentLike(HttpServletRequest request, @RequestBody JSONObject json) {
        if (!json.containsKey("commentId") || !json.containsKey("isLike")) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.ARGS_ERROR.statusCode()).resultJson();
        }
        TraceRequest req = Utils.getTraceRequest(request);
        return this.articleService.addCommentLike(req, json.getString("commentId"), json.getInteger("isLike"))
                .resultJson();
    }

    @RequestMapping(value = "api/v1/article/search", method = RequestMethod.GET)
    public String search(HttpServletRequest request, String keyword) {
        TraceRequest req = Utils.getTraceRequest(request);
        return this.articleService.search(req, keyword).resultJson();
    }
    /* ==========================分类栏目相关=============================== */

    @RequestMapping(value = "api/v1/article/columns", method = RequestMethod.GET)
    public String columns(HttpServletRequest request, String userId) {
        TraceRequest req = Utils.getTraceRequest(request);
        return this.articleService.columns(req, userId).resultJson();
    }

    @RequestMapping(value = "api/v1/article/column/{columnId}", method = RequestMethod.GET)
    public String articlesInColumn(HttpServletRequest request, String userId,
            @PathVariable(value = "columnId", required = true) String columnId) {
        TraceRequest req = Utils.getTraceRequest(request);
        return this.articleService.findListByColumn(req, userId, columnId).resultJson();
    }

    @RequestMapping(value = "api/v1/article/column/save", method = RequestMethod.POST)
    public String saveColumn(HttpServletRequest request, @RequestBody ArticleColumnVo column) {
        TraceRequest req = Utils.getTraceRequest(request);
        if (StringUtil.isNotBlank(column.getColumnId())) {
            return this.articleService.updateColumn(req, column.getColumnId(), column.getName()).resultJson();
        } else {
            return this.articleService.addColumn(req, column.getName()).resultJson();
        }
    }

    @RequestMapping(value = "api/v1/article/column/remove", method = RequestMethod.POST)
    public String removeColumn(HttpServletRequest request, @RequestBody JSONObject json) {
        if (!json.containsKey("columnId")) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.ARGS_ERROR.statusCode()).resultJson();
        }
        TraceRequest req = Utils.getTraceRequest(request);
        return this.articleService.removeColumn(req, json.getString("columnId")).resultJson();
    }

    @RequestMapping(value = "api/v1/article/into-column", method = RequestMethod.POST)
    public String intoColumn(HttpServletRequest request, @RequestBody JSONObject json) {
        if (!json.containsKey("columnId") || !json.containsKey("articleId")) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.ARGS_ERROR.statusCode()).resultJson();
        }
        TraceRequest req = Utils.getTraceRequest(request);
        return this.articleService.putOrTakeColumn(req, json.getString("columnId"), json.getString("articleId"))
                .resultJson();
    }
}