/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.simplenote.modules.common.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.xiaoyu.simplenote.common.base.BaseDao;
import com.xiaoyu.simplenote.modules.common.entity.File;

/**
 * @author hongyu
 * @date 2019-02
 * @description
 */
@Repository
public interface FileDao extends BaseDao<File>{

    public int batchInsert(List<File> list);
}
