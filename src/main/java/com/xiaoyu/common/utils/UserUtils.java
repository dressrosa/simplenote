/**
 * 唯有读书,不慵不扰
 */
package com.xiaoyu.common.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.xiaoyu.modules.biz.user.entity.User;

/**
 * @author hongyu
 * @date 2017-11-13 22:10
 * @description 一些用户权限的判断
 */
public class UserUtils {


    /**检查登录失效
     * @param request
     * @return
     */
    public static User checkLoginDead(HttpServletRequest request) {
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
        if (!userId.equals(user.getId())) {
            return null;
        }
        return user;
    }
}
