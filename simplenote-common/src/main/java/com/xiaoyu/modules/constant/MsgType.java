package com.xiaoyu.modules.constant;

/**
 * @author xiaoyu
 * @date 2018-01
 * @description 消息类型
 */
public enum MsgType {
    /**
     * 0, "消息"
     */
    NEWS(0, "消息"),
    /**
     * 1, "留言"
     */
    MESSAGE(1, "留言"),
    /**
     * 2, "通知"
     */
    NOTICE(2, "通知");

    private int code;
    private String message;

    MsgType(int code, String msg) {
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
