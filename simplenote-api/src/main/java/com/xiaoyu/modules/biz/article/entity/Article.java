/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.article.entity;

import com.xiaoyu.common.base.BaseEntity;

/**
 * @author xiaoyu 2016年3月29日
 */
public class Article extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String userId;
    private String title;
    private String content;
    private String brief;

    private String columnId;

    public String getBrief() {
        return brief;
    }

    public Article setBrief(String brief) {
        this.brief = brief;
        return this;
    }

    public String getColumnId() {
        return columnId;
    }

    public Article setColumnId(String columnId) {
        this.columnId = columnId;
        return this;
    }

    public String getUserId() {
        return this.userId;
    }

    public Article setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getTitle() {
        return this.title;
    }

    public Article setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContent() {
        return this.content;
    }

    public Article setContent(String content) {
        this.content = content;
        return this;
    }

}
