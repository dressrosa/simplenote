/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.common.base;

import java.io.Serializable;
import java.util.Date;

/**
 * 基本实体类参数配置
 * 
 * @author xiaoyu 2016年3月22日
 */
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 唯一主键
     */
    private String id;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 更新时间
     */
    private Date updateDate;

    /**
     * 删除标志
     */
    private int delFlag;

    public String getId() {
        return this.id;
    }

    public BaseEntity setId(String id) {
        this.id = id;
        return this;
    }

    public Date getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return this.updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public int getDelFlag() {
        return this.delFlag;
    }

    public void setDelFlag(int delFlag) {
        this.delFlag = delFlag;
    }

}
