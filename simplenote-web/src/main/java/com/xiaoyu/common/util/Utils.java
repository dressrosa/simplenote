package com.xiaoyu.common.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.xiaoyu.common.request.TraceRequest;
import com.xiaoyu.common.request.TraceRequest.Header;
import com.xiaoyu.common.utils.StringUtil;
import com.xiaoyu.modules.biz.user.entity.User;

/**
 * @author hongyu
 * @date 2018-08
 * @description
 */
public class Utils {

    public static User checkLogin(HttpServletRequest request) {
        final String userId = request.getHeader("userId");
        final String token = request.getHeader("token");
        final HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        final User user = (User) session.getAttribute(token);
        if (user == null) {
            return null;
        }
        if (!userId.equals(user.getUuid())) {
            return null;
        }
        return user;
    }

    public static TraceRequest getTraceRequest(HttpServletRequest request) {
        TraceRequest req = new TraceRequest();
        final String userId = request.getHeader("userId");
        final String token = request.getHeader("token");
        Header header = new Header();
        req.setHeader(header.setToken(token));
        if (StringUtil.isNotEmpty(request.getHeader("pageNum"))) {
            header.setPageNum(Integer.valueOf(request.getHeader("pageNum")))
                    .setPageSize(Integer.valueOf(request.getHeader("pageSize")));
        }
        final HttpSession session = request.getSession(false);
        if (session == null) {
            req.setLogin(false);
        } else {
            final User user = (User) session.getAttribute(token);
            if (user == null) {
                req.setLogin(false);
            } else if (!userId.equals(user.getUuid())) {
                req.setLogin(false);
            } else {
                req.setLogin(true);
                req.setUser(user);
            }
        }
        return req;
    }

    public static boolean isPC(HttpServletRequest request) {
        final String userAgentInfo = request.getHeader("user-agent");
        final String[] agents = { "Android", "iPhone", "SymbianOS", "Windows Phone", "iPad", "iPod" };
        boolean flag = true;
        for (int v = 0; v < agents.length; v++) {
            if (userAgentInfo.indexOf(agents[v]) > 0) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    /**
     * 获得用户远程地址
     */
    public static String getRemoteAddr(HttpServletRequest request) {
        String remoteAddr = request.getHeader("X-Real-IP");
        if (StringUtil.isNotBlank(remoteAddr)) {
            remoteAddr = request.getHeader("X-Forwarded-For");
        } else if (StringUtil.isNotBlank(remoteAddr)) {
            remoteAddr = request.getHeader("Proxy-Client-IP");
        } else if (StringUtil.isNotBlank(remoteAddr)) {
            remoteAddr = request.getHeader("WL-Proxy-Client-IP");
        }
        return remoteAddr != null ? remoteAddr : request.getRemoteAddr();
    }

    public static boolean isSafeRequest(HttpServletRequest request) {
        final String userAgentInfo = request.getHeader("user-agent");
        if (StringUtil.isBlank(userAgentInfo)) {
            return false;
        }
        return true;
    }
}
