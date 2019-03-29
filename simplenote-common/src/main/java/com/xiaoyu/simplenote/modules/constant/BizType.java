package com.xiaoyu.simplenote.modules.constant;

/**
 * @author xiaoyu
 * @date 2018-01
 * @description 业务类型
 */
public enum BizType {
    /**
     * 0, "文章"
     */
    ARTICLE(0, "文章"),
    /**
     * 1, "用户"
     */
    USER(1, "用户"),

    NOTE(2, "纸条");

    private int code;
    private String message;

    BizType(int code, String msg) {
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
