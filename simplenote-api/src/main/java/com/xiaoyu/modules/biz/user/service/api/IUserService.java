/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.user.service.api;

import java.util.List;

import com.xiaoyu.common.base.ResponseMapper;
import com.xiaoyu.common.request.TraceRequest;
import com.xiaoyu.modules.biz.user.entity.Follow;
import com.xiaoyu.modules.biz.user.entity.User;
import com.xiaoyu.modules.biz.user.vo.UserVo;

/**
 * @author hongyu
 * @date 2018-02
 * @description
 */
public interface IUserService {

    /**
     * 登录
     * 
     * @param loginName
     * @param password
     * @return
     */
    public User login(String loginName, String password);

    /**
     * 登录记录
     * 
     * @param request
     * @param userId
     * @param device
     * @return
     */
    public ResponseMapper loginRecord(TraceRequest request, String userId, String device);

    /**
     * 用户详情
     * 
     * @param request
     * @param userId
     * @return
     */
    public ResponseMapper userDetail(TraceRequest request, String userId);

    /**
     * 注册
     * 
     * @param request
     * @param loginName
     * @param password
     * @return
     */
    public ResponseMapper register(TraceRequest request, String loginName, String password);

    /**
     * 编辑用户
     * 
     * @param request
     * @param userId
     * @param content
     * @param flag
     * @return
     */
    public ResponseMapper editUser(TraceRequest request, String content, Integer flag);

    /**
     * 关注用户
     * 
     * @param request
     * @param userId
     * @param followTo
     * @return
     */
    public ResponseMapper followUser(TraceRequest request, String userId, String followTo);

    /**
     * 取消关注
     * 
     * @param request
     * @param userId
     * @param followTo
     * @return
     */
    public ResponseMapper cancelFollow(TraceRequest request, String userId, String followTo);

    /**
     * 是否关注
     * 
     * @param request
     * @param userId
     * @param followTo
     * @return
     */
    public ResponseMapper isFollowed(TraceRequest request, String userId, String followTo);

    /**
     * 我的关注者
     * 
     * @param request
     * @param userId
     * @return
     */
    public ResponseMapper follower(TraceRequest request, String userId);

    /**
     * 我关注的人
     * 
     * @param request
     * @param userId
     * @return
     */
    public ResponseMapper following(TraceRequest request, String userId);

    /**
     * 通用的统计数
     * 
     * @param request
     * @param userId
     * @return
     */
    public ResponseMapper commonNums(TraceRequest request, String userId);

    public User getByUuid(String uuid);

    public List<UserVo> findVoByUuid(List<String> userIdList);

    public int addNum(int type, int num, String userId);

    public UserVo getVoByUuid(String userId);

    public Follow getFollow(String followId);
}
