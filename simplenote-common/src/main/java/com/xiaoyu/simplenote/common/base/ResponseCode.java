package com.xiaoyu.simplenote.common.base;

/**
 * @author hongyu
 * @date 2018-01
 * @description 请求返回码
 */
public enum ResponseCode {

    /**
     * 0,"success"
     */
    SUCCESS(0, "success"),
    /**
     * 1,"params error"
     */
    ARGS_ERROR(1, "params error"),
    /**
     * 2,"no data"
     */
    NO_DATA(2, "no data"),
    /**
     * 3,"business failed"
     */
    FAILED(3, "business failed"),
    /**
     * 4,"data exist"
     */
    EXIST(4, "data exist"),

    /**
     * 登陆失效
     */
    LOGIN_INVALIDATE(20001, "login expire"),
    /**
     * 没有权限
     */
    REQ_NOACCESS(20002, "no access");

    private int code;
    private String message;

    ResponseCode(int code, String msg) {
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
