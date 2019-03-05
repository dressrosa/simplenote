/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.note.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.xiaoyu.common.base.BaseDao;
import com.xiaoyu.modules.biz.note.entity.NoteComment;
import com.xiaoyu.modules.biz.note.vo.NoteCommentVo;

@Repository
public interface NoteCommentDao extends BaseDao<NoteComment> {

    List<NoteCommentVo> findList(String noteId);

    int predo();

}
