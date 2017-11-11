/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.message.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.xiaoyu.common.base.BaseDao;
import com.xiaoyu.modules.biz.message.entity.Message;
import com.xiaoyu.modules.biz.message.vo.MessageVo;

/**
 * 2017年7月21日下午4:04:17
 * 
 * @author xiaoyu
 * @description
 */
@Repository
public interface MessageDao extends BaseDao<Message> {

    int isDoAgain(Message t);

    List<MessageVo> findVoByList(Message msg);

    int read(List<String> idsList);

    int getUnreadNumBefore1Hour(@Param("receiverId") String userId);
}
