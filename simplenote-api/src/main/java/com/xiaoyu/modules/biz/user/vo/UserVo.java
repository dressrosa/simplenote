/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.user.vo;

import com.xiaoyu.modules.biz.user.entity.UserAttr;

/**
 * @author xiaoyu 2016年3月16日
 */
public class UserVo {

    private String userId;
    private String nickname;
    private String avatar;
    private Integer sex;
    private String background;
    private String description;
    private String signature;
    private UserAttr attr;

    public String getBackground() {
        return this.background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public UserAttr getAttr() {
        return this.attr;
    }

    public void setAttr(UserAttr attr) {
        this.attr = attr;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return this.avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getSex() {
        return this.sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSignature() {
        return this.signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

}
