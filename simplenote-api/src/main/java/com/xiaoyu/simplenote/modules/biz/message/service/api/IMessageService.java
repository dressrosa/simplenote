/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.simplenote.modules.biz.message.service.api;

import com.xiaoyu.simplenote.common.base.ResponseMapper;
import com.xiaoyu.simplenote.common.request.TraceRequest;
import com.xiaoyu.simplenote.modules.biz.message.entity.Message;

/**
 * 消息相关
 * 
 * @author hongyu
 * @date 2018-02
 * @description
 */
public interface IMessageService {

    /**
     * 消息列表
     * 
     * @param request
     * @param userId
     * @param type
     * @return
     */
    public ResponseMapper getMsgByType(TraceRequest request, String userId, int type);

    /**
     * 回复消息
     * 
     * @param request
     * @param msgId
     * @param replyContent
     * @return
     */
    public ResponseMapper replyMsg(TraceRequest request, String msgId, String replyContent);

    /**
     * 读消息(动作)
     * 
     * @param request
     * @param msgIds
     * @return
     */
    public ResponseMapper read(TraceRequest request, String msgIds);

    /**
     * 未读消息数
     * 
     * @param request
     * @return
     */
    public ResponseMapper unreadNum(TraceRequest request);

    /**
     * 推送消息事件
     * 
     * @param message
     * @return
     */
    public ResponseMapper sendMsgEvent(Message message);

    /**
     * @param request
     * @param message
     * @return
     */
    public ResponseMapper removeMessage(TraceRequest request, Message... message);

    public int isDoAgain(Message t);

    public int insert(Message msg);
}
