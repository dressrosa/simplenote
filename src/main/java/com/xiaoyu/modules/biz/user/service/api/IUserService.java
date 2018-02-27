/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.user.service.api;

import javax.servlet.http.HttpServletRequest;

/**
 * @author hongyu
 * @date 2018-02
 * @description
 */
public interface IUserService {

    /**
     * 登录
     * 
     * @param request
     * @param loginName
     * @param password
     * @return
     */
    public String login(HttpServletRequest request, String loginName, String password);

    /**
     * 登录记录
     * 
     * @param request
     * @param userId
     * @param device
     * @return
     */
    public String loginRecord(HttpServletRequest request, String userId, String device);

    /**
     * 用户详情
     * 
     * @param request
     * @param userId
     * @return
     */
    public String userDetail(HttpServletRequest request, String userId);

    /**
     * 注册
     * 
     * @param request
     * @param loginName
     * @param password
     * @return
     */
    public String register(HttpServletRequest request, String loginName, String password);

    /**
     * 编辑用户
     * 
     * @param request
     * @param userId
     * @param content
     * @param flag
     * @return
     */
    public String editUser(HttpServletRequest request, String content, Integer flag);

    /**
     * 关注用户
     * 
     * @param request
     * @param userId
     * @param followTo
     * @return
     */
    public String followUser(HttpServletRequest request, String userId, String followTo);

    /**
     * 取消关注
     * 
     * @param request
     * @param userId
     * @param followTo
     * @return
     */
    public String cancelFollow(HttpServletRequest request, String userId, String followTo);

    /**
     * 是否关注
     * 
     * @param request
     * @param userId
     * @param followTo
     * @return
     */
    public String isFollowed(HttpServletRequest request, String userId, String followTo);

    /**
     * 我的关注者
     * 
     * @param request
     * @param userId
     * @return
     */
    public String follower(HttpServletRequest request, String userId);

    /**
     * 我关注的人
     * 
     * @param request
     * @param userId
     * @return
     */
    public String following(HttpServletRequest request, String userId);

    /**
     * 通用的统计数
     * 
     * @param request
     * @param userId
     * @return
     */
    public String commonNums(HttpServletRequest request, String userId);
}
