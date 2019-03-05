/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.common.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.xiaoyu.common.base.BaseDao;
import com.xiaoyu.modules.common.entity.File;

/**
 * @author hongyu
 * @date 2019-02
 * @description
 */
@Repository
public interface FileDao extends BaseDao<File>{

    public int batchInsert(List<File> list);
}
