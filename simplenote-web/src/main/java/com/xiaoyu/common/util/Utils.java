package com.xiaoyu.common.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.xiaoyu.common.request.TraceRequest;
import com.xiaoyu.common.request.TraceRequest.Header;
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
        req.setHeader(new Header().setToken(token)
                .setPageNum(request.getHeader("pageNum"))
                .setPageSize(request.getHeader("pageSize")));
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

}
