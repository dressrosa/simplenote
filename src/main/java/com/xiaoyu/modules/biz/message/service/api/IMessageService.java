/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.message.service.api;

import javax.servlet.http.HttpServletRequest;

public interface IMessageService {

    /**
     * 消息列表
     * 
     * @param request
     * @param userId
     * @param type
     * @return
     */
    public String getMsgByType(HttpServletRequest request, String userId, int type);

    /**
     * 删除消息
     * 
     * @param request
     * @param msgId
     * @return
     */
    public String removeMsg(HttpServletRequest request, String msgId);

    /**
     * 回复消息
     * 
     * @param request
     * @param msgId
     * @param reply
     * @return
     */
    public String replyMsg(HttpServletRequest request, String msgId, String reply);

    /**
     * 读消息(动作)
     * 
     * @param request
     * @param msgIds
     * @return
     */
    public String read(HttpServletRequest request, String msgIds);

    /**
     * 未读消息数
     * 
     * @param request
     * @return
     */
    public String unreadNum(HttpServletRequest request);
}
