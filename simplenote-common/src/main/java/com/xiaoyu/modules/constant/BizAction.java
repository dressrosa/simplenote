package com.xiaoyu.modules.constant;

/**
 * @author xiaoyu
 * @date 2018-01
 * @description 业务动作
 */
public enum BizAction {
    /**
     * 0, "无"
     */
    NONE(0, "无"),
    /**
     * 1, "评论"
     */
    COMMENT(1, "评论"),
    /**
     * 2, "赞文章"
     */
    ARTICLE_LIKE(2, "赞文章"),
    /**
     * 3, "收藏"
     */
    COLLECT(3, "收藏"),
    /**
     * 4, "回复"
     */
    REPLY(4, "回复"),
    /**
     * 5, "评论@"
     */
    COMMENT_AT(5, "评论@"),
    /**
     * 6, "赞评论"
     */
    COMMENT_LIKE(6, "赞评论"),
    /**
     * 7, "留言"
     */
    MESSAGE(7, "留言"),

    /**
     * 8, "关注用户"
     */
    FOLLOW(8, "关注用户");

    private int code;
    private String message;

    BizAction(int code, String msg) {
        this.code = code;
        this.message = msg;
    }

    public Integer statusCode() {
        return this.code;
    }

    public String statusMsg() {
        return this.message;
    }
}
