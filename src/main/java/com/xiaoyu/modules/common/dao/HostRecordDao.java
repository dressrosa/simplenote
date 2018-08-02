/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.common.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.xiaoyu.modules.common.entity.HostRecord;

/**
 * @author hongyu
 * @date 2018-08
 * @description
 */
@Repository
public interface HostRecordDao {

    public int batchInsert(List<HostRecord> list);

    public int batchUpdate(List<HostRecord> list);

    public List<HostRecord> queryByInterval(long time);
}
