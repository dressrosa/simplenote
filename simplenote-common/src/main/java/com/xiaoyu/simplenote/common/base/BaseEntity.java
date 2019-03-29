/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.simplenote.common.base;

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
    private Long id;

    private String uuid;

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

    /**
     * 乐观锁要被更新的老数据
     */
    private Long old;

    public Long getOld() {
        return old;
    }

    public BaseEntity setOld(Long old) {
        this.old = old;
        return this;
    }

    public Long getId() {
        return this.id;
    }

    public BaseEntity setId(long id) {
        this.id = id;
        return this;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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
