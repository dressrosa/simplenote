/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */ 
package com.xiaoyu.modules.biz.user.dao;


import org.springframework.stereotype.Repository;
import com.xiaoyu.common.base.BaseDao;
import com.xiaoyu.modules.biz.user.entity.User;

/** 
 * @author xiaoyu
 *2016年3月16日
 *指定注解 不用@component
 */
@Repository
public interface UserDao extends BaseDao<User> {

	public User getForLogin(User user);
	
}
