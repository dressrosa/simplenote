/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.simplenote.modules.biz.user.entity;

import com.xiaoyu.simplenote.common.base.BaseEntity;

/**
 * @author xiaoyu 2016年3月16日
 */
public class User extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String nickname;
    private String loginName;
    private String avatar;
    private Integer sex;
    private String description;
    private String password;
    private String signature;
    private String background;

    public String getBackground() {
        return this.background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getSignature() {
        return this.signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public Integer getSex() {
        return this.sex;
    }

    public User setSex(Integer sex) {
        this.sex = sex;
        return this;
    }

    public String getPassword() {
        return this.password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvatar() {
        return this.avatar;
    }

    public User setAvatar(String avatar) {
        this.avatar = avatar;
        return this;
    }

    public String getNickname() {
        return this.nickname;
    }

    public User setNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public String getLoginName() {
        return this.loginName;
    }

    public User setLoginName(String loginName) {
        this.loginName = loginName;
        return this;
    }

}
