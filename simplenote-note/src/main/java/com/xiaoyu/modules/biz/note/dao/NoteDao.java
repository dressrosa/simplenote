/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.note.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.xiaoyu.common.base.BaseDao;
import com.xiaoyu.modules.biz.note.entity.Note;

/**
 * @author hongyu
 * @date 2019-02
 * @description
 */
@Repository
public interface NoteDao extends BaseDao<Note> {

    Note getVoByUuid(@Param("uuid") String uuid);

}
