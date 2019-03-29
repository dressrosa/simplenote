/*
 *  Copyright 20016-2016 Edencity, Inc.
 */
package com.xiaoyu.simplenote.modules.biz.user.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.xiaoyu.simplenote.common.base.BaseDao;
import com.xiaoyu.simplenote.modules.biz.user.entity.Follow;
import com.xiaoyu.simplenote.modules.biz.user.vo.FollowVo;

/**
 * 2017年6月28日下午5:10:33
 * 
 * @author xiaoyu
 * @description 关注用户
 */
public interface FollowDao extends BaseDao<Follow> {

    int cancelFollow(@Param("userId") String userId, @Param("followerId") String followerId);

    List<FollowVo> findList(Follow f);

    int isFollow(@Param("followerId") String userId, @Param("userId") String followTo);
}
