/**
 * 
 */
package com.xiaoyu.simplenote.modules.common.entity;

/**
 * @author hongyu
 * @date 2018-08
 * @description
 */
public class HostRecord {

    private int id;
    private String ip;
    private String uri;
    private long createDate;
    private String location;

    public int getId() {
        return id;
    }

    public HostRecord setId(int id) {
        this.id = id;
        return this;
    }

    public String getLocation() {
        return location;
    }

    public HostRecord setLocation(String location) {
        this.location = location;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public HostRecord setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public String getUri() {
        return uri;
    }

    public HostRecord setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public long getCreateDate() {
        return createDate;
    }

    public HostRecord setCreateDate(long createDate) {
        this.createDate = createDate;
        return this;
    }

}
