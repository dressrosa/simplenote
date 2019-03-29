/**
 * 唯有读书,不慵不扰
 */
package com.xiaoyu.simplenote.modules.biz.article.entity;

import com.xiaoyu.simplenote.common.base.BaseEntity;

/**
 * @author hongyu
 * @date 2018-05
 * @description
 */
public class ArticleColumn extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String userId;
    private String name;
    private Integer isOpen;

    public String getUserId() {
        return userId;
    }

    public ArticleColumn setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getName() {
        return name;
    }

    public ArticleColumn setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getIsOpen() {
        return isOpen;
    }

    public ArticleColumn setIsOpen(Integer isOpen) {
        this.isOpen = isOpen;
        return this;
    }

}
