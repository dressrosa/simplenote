/**
 * 唯有读书,不慵不扰
 */
package com.xiaoyu.modules.biz.article.vo;

/**
 * @author hongyu
 * @date 2018-05
 * @description
 */
public class ArticleColumnVo {

    private String columnId;
    private String userId;
    private String name;
    private int isOpen;

    private String userName;

    
    public String getColumnId() {
        return columnId;
    }

    public ArticleColumnVo setColumnId(String columnId) {
        this.columnId = columnId;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public ArticleColumnVo setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public ArticleColumnVo setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getName() {
        return name;
    }

    public ArticleColumnVo setName(String name) {
        this.name = name;
        return this;
    }

    public int getIsOpen() {
        return isOpen;
    }

    public ArticleColumnVo setIsOpen(int isOpen) {
        this.isOpen = isOpen;
        return this;
    }

}
