/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.note.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.xiaoyu.common.base.BaseDao;
import com.xiaoyu.modules.biz.note.entity.NoteAttr;

@Repository
public interface NoteAttrDao extends BaseDao<NoteAttr> {

    NoteAttr getByArticleId(@Param("noteId") String noteId);

    /**
     * 加法更新
     * 
     * @param attr
     * @return
     */
    int updateByAddition(NoteAttr attr);
}
