/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.note.service.api;

import com.xiaoyu.common.base.ResponseMapper;
import com.xiaoyu.common.request.TraceRequest;
import com.xiaoyu.modules.biz.note.entity.Note;

/**
 * @author hongyu
 * @date 2019-02
 * @description
 */
public interface INoteService {

    public ResponseMapper squareList(TraceRequest request);

    public ResponseMapper listOfUser(TraceRequest request, String userId);

    ResponseMapper addNote(TraceRequest request, Note note, String files);

    ResponseMapper userComments(TraceRequest request, String userId);

    ResponseMapper comment(TraceRequest request, String noteId, String content);

    ResponseMapper reply(TraceRequest request, String commentId, String replyContent);

    ResponseMapper comments(TraceRequest request, String noteId);

    ResponseMapper readDestination(TraceRequest request, String noteId);
    
    public ResponseMapper addLike(TraceRequest request, String noteId);

    ResponseMapper removeNote(TraceRequest request, String noteId);
}
