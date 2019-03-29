/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.simplenote.modules.biz.user.entity;

import com.xiaoyu.simplenote.common.base.BaseEntity;

/**
 * @author xiaoyu 2016年4月12日
 */
public class LoginRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String userId;
    private String loginIp;
    private String device;

    public String getDevice() {
        return this.device;
    }

    public LoginRecord setDevice(String device) {
        this.device = device;
        return this;
    }

    public String getUserId() {
        return this.userId;
    }

    public LoginRecord setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getLoginIp() {
        return this.loginIp;
    }

    public LoginRecord setLoginIp(String loginIp) {
        this.loginIp = loginIp;
        return this;
    }

}
