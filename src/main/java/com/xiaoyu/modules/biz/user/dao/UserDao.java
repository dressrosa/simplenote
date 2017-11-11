/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.user.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.xiaoyu.common.base.BaseDao;
import com.xiaoyu.modules.biz.user.entity.User;
import com.xiaoyu.modules.biz.user.vo.UserVo;

/**
 * @author xiaoyu 2016年3月16日 指定注解 不用@component
 */
@Repository
public interface UserDao extends BaseDao<User> {

    public User getForLogin(User user);

    public UserVo getVo(User user);

    public UserVo getVoById(@Param("id") String id);
}
