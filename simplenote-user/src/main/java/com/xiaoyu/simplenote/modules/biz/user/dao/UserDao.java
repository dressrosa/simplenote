/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.simplenote.modules.biz.user.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.xiaoyu.simplenote.common.base.BaseDao;
import com.xiaoyu.simplenote.modules.biz.user.entity.User;
import com.xiaoyu.simplenote.modules.biz.user.vo.UserVo;

/**
 * @author xiaoyu 2016年3月16日 指定注解 不用@component
 */
@Repository
public interface UserDao extends BaseDao<User> {

    public User getForLogin(User user);

    public UserVo getVo(User user);

    public UserVo getVoByUuid(@Param("uuid") String uuid);

    public List<UserVo> findVoByUuid(List<String> userIdList);
}
