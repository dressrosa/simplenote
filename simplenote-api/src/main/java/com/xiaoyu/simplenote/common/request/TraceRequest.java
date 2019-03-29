/**
 * 
 */
package com.xiaoyu.simplenote.common.request;

import java.io.Serializable;

import com.xiaoyu.simplenote.modules.biz.user.entity.User;

/**
 * @author hongyu
 * @date 2018-08
 * @description
 */
public class TraceRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean login;

    private Header header;
    // 登录者
    private User user;

    public Header getHeader() {
        return header;
    }

    public TraceRequest setHeader(Header header) {
        this.header = header;
        return this;
    }

    public boolean isLogin() {
        return login;
    }

    public TraceRequest setLogin(boolean login) {
        this.login = login;
        return this;
    }

    public User getUser() {
        return user;
    }

    public TraceRequest setUser(User user) {
        this.user = user;
        return this;
    }

    public static class Header {
        private Integer pageNum;
        private Integer pageSize;
        private String token;
        private String remoteHost;

        public String getRemoteHost() {
            return remoteHost;
        }

        public Header setRemoteHost(String remoteHost) {
            this.remoteHost = remoteHost;
            return this;
        }

        public Integer getPageNum() {
            return pageNum;
        }

        public Header setPageNum(Integer pageNum) {
            this.pageNum = pageNum;
            return this;
        }

        public Integer getPageSize() {
            return pageSize;
        }

        public Header setPageSize(Integer pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public String getToken() {
            return token;
        }

        public Header setToken(String token) {
            this.token = token;
            return this;
        }

    }
}
