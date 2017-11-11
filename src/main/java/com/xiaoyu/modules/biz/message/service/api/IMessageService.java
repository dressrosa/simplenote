/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.message.service.api;

import javax.servlet.http.HttpServletRequest;

public interface IMessageService {

    public String getMsgByType(HttpServletRequest request, String userId, int type);

    public String removeMsg(HttpServletRequest request, String msgId);

    public String replyMsg(HttpServletRequest request, String msgId, String reply);

    public String read(HttpServletRequest request, String msgIds);

    public String unreadNum(HttpServletRequest request);
}
