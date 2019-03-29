/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.simplenote.modules.biz.note.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xiaoyu.beacon.starter.anno.BeaconExporter;
import com.xiaoyu.maple.core.MapleUtil;
import com.xiaoyu.simplenote.common.base.CommonQuery;
import com.xiaoyu.simplenote.common.base.ResponseCode;
import com.xiaoyu.simplenote.common.base.ResponseMapper;
import com.xiaoyu.simplenote.common.request.TraceRequest;
import com.xiaoyu.simplenote.common.utils.IdGenerator;
import com.xiaoyu.simplenote.common.utils.RedisLock;
import com.xiaoyu.simplenote.common.utils.SpringBeanUtils;
import com.xiaoyu.simplenote.common.utils.StringUtil;
import com.xiaoyu.simplenote.common.utils.TimeUtils;
import com.xiaoyu.simplenote.modules.biz.message.entity.Message;
import com.xiaoyu.simplenote.modules.biz.message.service.api.IMessageService;
import com.xiaoyu.simplenote.modules.biz.note.dao.NoteAttrDao;
import com.xiaoyu.simplenote.modules.biz.note.dao.NoteCommentDao;
import com.xiaoyu.simplenote.modules.biz.note.dao.NoteDao;
import com.xiaoyu.simplenote.modules.biz.note.dao.NoteDestinationDao;
import com.xiaoyu.simplenote.modules.biz.note.dao.NoteLikeDao;
import com.xiaoyu.simplenote.modules.biz.note.entity.Note;
import com.xiaoyu.simplenote.modules.biz.note.entity.NoteAttr;
import com.xiaoyu.simplenote.modules.biz.note.entity.NoteComment;
import com.xiaoyu.simplenote.modules.biz.note.entity.NoteDestination;
import com.xiaoyu.simplenote.modules.biz.note.entity.NoteLike;
import com.xiaoyu.simplenote.modules.biz.note.service.api.INoteService;
import com.xiaoyu.simplenote.modules.biz.note.vo.NoteCommentVo;
import com.xiaoyu.simplenote.modules.biz.user.entity.User;
import com.xiaoyu.simplenote.modules.biz.user.service.api.IUserService;
import com.xiaoyu.simplenote.modules.biz.user.vo.UserVo;
import com.xiaoyu.simplenote.modules.common.entity.File;
import com.xiaoyu.simplenote.modules.common.service.api.IFileService;
import com.xiaoyu.simplenote.modules.constant.BizAction;
import com.xiaoyu.simplenote.modules.constant.BizType;
import com.xiaoyu.simplenote.modules.constant.MsgType;

/**
 * @author hongyu
 * @date 2019-02
 * @description
 */
@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
@Primary
@BeaconExporter(interfaceName = "com.xiaoyu.simplenote.modules.biz.note.service.api.INoteService", group = "dev")
public class NoteServiceImpl implements INoteService {

    @Autowired
    private IUserService userService;

    @Autowired
    private IFileService fileService;

    @Autowired
    private NoteDao noteDao;

    @Autowired
    private NoteAttrDao noteAttrDao;

    @Autowired
    private NoteLikeDao likeDao;

    @Autowired
    private NoteCommentDao noteCommentDao;

    @Autowired
    private NoteDestinationDao noteDestinationDao;

    @Autowired(required = false)
    private IMessageService messageService;

    @Override
    public ResponseMapper squareList(TraceRequest request) {
        int pageNum = request.getHeader().getPageNum();
        int pageSize = request.getHeader().getPageSize();
        pageSize = pageSize > 32 ? 32 : pageSize;

        PageHelper.startPage(pageNum, pageSize);
        List<Note> list = this.noteDao.findByList(new CommonQuery());
        if (list.isEmpty()) {
            return ResponseMapper.createMapper().code(ResponseCode.NO_DATA.statusCode());
        }
        return ResponseMapper.createMapper().data(this.handleNotes(request, list));
    }

    private List<Map<String, Object>> handleNotes(TraceRequest request, List<Note> list) {
        if (list.isEmpty()) {
            return new ArrayList<>(0);
        }
        List<Map<String, Object>> voList = new ArrayList<>(list.size());

        List<String> userIds = list.stream().map(Note::getUserId).collect(Collectors.toList());
        List<UserVo> userVos = this.userService.findVoByUuid(userIds);
        Map<String, UserVo> userVoMap = userVos.stream().collect(Collectors.toMap(UserVo::getUserId, vo -> vo));

        List<String> noteIds = list.stream().map(Note::getUuid).collect(Collectors.toList());
        List<File> files = this.fileService.queryFilesByBizIds(noteIds);
        Map<String, List<File>> fileMap = files.stream().collect(Collectors.groupingBy(File::getBizId));

        List<NoteAttr> attrs = new ArrayList<>();
        if (!noteIds.isEmpty()) {
            attrs = this.noteAttrDao.findByList(new CommonQuery().setBizIds(noteIds));
        }
        Map<String, NoteAttr> attrMap = new HashMap<>(attrs.size());
        if (!attrs.isEmpty()) {
            attrMap.putAll(attrs.stream().collect(Collectors.toMap(NoteAttr::getNoteId, v -> v)));
        }

        Map<String, NoteLike> likeMap = new HashMap<>();
        if (request.isLogin()) {
            List<NoteLike> likeList = this.likeDao.findByList(new CommonQuery()
                    .setUserId(request.getUser().getUuid())
                    .setBizIds(noteIds));
            likeList.forEach(a -> {
                likeMap.put(a.getNoteId(), a);
            });
        }
        Date now = new Date();
        list.forEach(a -> {
            List<File> m = fileMap.get(a.getUuid());
            Map<String, Object> voMap = MapleUtil.wrap().stick("user", userVoMap.get(a.getUserId()))
                    .stick("noteId", a.getUuid())
                    .stick("attr", attrMap.get(a.getUuid()))
                    .stick("content", a.getContent())
                    .stick("location", a.getLocation())
                    .stick("createTime", TimeUtils.getChineseForInterval(a.getCreateDate(), now))
                    .stick("files", m)
                    .stick("isLike", 0)
                    .map();
            NoteLike nl = likeMap.get(a.getUuid());
            if (nl != null) {
                voMap.put("isLike", Integer.toString(nl.getStatus()));
            }
            voList.add(voMap);
        });
        return voList;
    }

    @Override
    public ResponseMapper listOfUser(TraceRequest request, String userId) {
        if (StringUtils.isEmpty(userId)) {
            return ResponseMapper.createMapper().code(ResponseCode.ARGS_ERROR.statusCode());
        }
        int pageNum = request.getHeader().getPageNum();
        int pageSize = request.getHeader().getPageSize();
        pageSize = pageSize > 32 ? 32 : pageSize;

        CommonQuery query = new CommonQuery();
        query.setUserId(userId);
        PageHelper.startPage(pageNum, pageSize);
        List<Note> list = this.noteDao.findByList(query);
        if (list.isEmpty()) {
            return ResponseMapper.createMapper().code(ResponseCode.NO_DATA.statusCode());
        }
        return ResponseMapper.createMapper().data(this.handleNotesOfUser(request, list, userId));
    }

    private List<Map<String, Object>> handleNotesOfUser(TraceRequest request, List<Note> list, String userId) {
        UserVo userVo = this.userService.getVoByUuid(userId);
        List<String> noteIds = list.stream().map(Note::getUuid).collect(Collectors.toList());
        List<File> files = this.fileService.queryFilesByBizIds(noteIds);
        Map<String, List<File>> fileMap = files.stream().collect(Collectors.groupingBy(File::getBizId));

        List<NoteAttr> attrs = new ArrayList<>();
        if (!noteIds.isEmpty()) {
            attrs = this.noteAttrDao.findByList(new CommonQuery().setBizIds(noteIds));
        }
        Map<String, NoteAttr> attrMap = attrs.stream().collect(Collectors.toMap(NoteAttr::getNoteId, v -> v));

        Map<String, NoteLike> likeMap = new HashMap<>();
        if (request.isLogin()) {
            List<NoteLike> likeList = this.likeDao.findByList(new CommonQuery()
                    .setUserId(request.getUser().getUuid())
                    .setBizIds(noteIds));
            likeList.forEach(a -> {
                likeMap.put(a.getNoteId(), a);
            });
        }

        List<Map<String, Object>> voList = new ArrayList<>(list.size());
        Date now = new Date();
        list.forEach(a -> {
            Map<String, Object> voMap = MapleUtil.wrap().stick("user", userVo)
                    .stick("noteId", a.getUuid())
                    .stick("attr", attrMap.get(a.getUuid()))
                    .stick("content", a.getContent())
                    .stick("location", a.getLocation())
                    .stick("createTime", TimeUtils.getChineseForInterval(a.getCreateDate(), now))
                    .stick("files", fileMap.get(a.getUuid()))
                    .stick("isLike", 0)
                    .map();
            NoteLike nl = likeMap.get(a.getUuid());
            if (nl != null) {
                voMap.put("isLike", Integer.toString(nl.getStatus()));
            }
            voList.add(voMap);
        });
        return voList;
    }

    @Override
    public ResponseMapper addNote(TraceRequest request, Note note, String files) {
        if (!request.isLogin()) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.LOGIN_INVALIDATE.statusCode())
                    .message("登录失效,请重新登录");
        }
        User user = request.getUser();
        Note t = new Note();
        RedisLock lock = RedisLock.getRedisLock("addNote:" + user.getUuid());
        String uuid = lock.lockGracefully(() -> {
            t.setContent(note.getContent())
                    .setLocation(note.getLocation())
                    .setUserId(user.getUuid())
                    .setUuid(IdGenerator.uuid());
            String[] fs = files.split(";");
            List<File> fList = new ArrayList<>(fs.length);
            if (fs.length > 0 && !StringUtils.isEmpty(files)) {
                File f = null;
                for (int i = 0; i < fs.length; i++) {
                    f = new File();
                    f.setBizId(t.getUuid())
                            .setBizType(0)
                            .setFileType(0)
                            .setName("")
                            .setUrl(fs[i].substring(fs[i].lastIndexOf("/")))
                            .setUserId(t.getUserId())
                            .setUuid(IdGenerator.uuid());
                    fList.add(f);
                }
            }
            SpringBeanUtils.getBean(NoteServiceImpl.class)
                    .doInsertNote(t, fList);
            return t.getUuid();
        });
        if (uuid == null) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.FAILED.statusCode())
                    .message("发表失败");
        }
        return ResponseMapper.createMapper().data(uuid);
    }

    @Transactional(readOnly = false, rollbackFor = RuntimeException.class)
    public void doInsertNote(Note t, List<File> fList) {
        NoteAttr attr = new NoteAttr().setCommentNum(0)
                .setLikeNum(0)
                .setMarkNum(0)
                .setReadNum(0)
                .setNoteId(t.getUuid());
        if (fList.isEmpty()) {
            this.noteAttrDao.insert(attr);
            this.noteDao.insert(t);
        } else {
            int ret = this.fileService.saveFiles(fList);
            if (ret > 0) {
                this.noteAttrDao.insert(attr);
                this.noteDao.insert(t);
            }
        }
    }

    @Override
    public ResponseMapper comment(TraceRequest request, String noteId, String content) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        if (!request.isLogin()) {
            return mapper.code(ResponseCode.LOGIN_INVALIDATE.statusCode());
        }
        if (StringUtil.isEmpty(content)) {
            return mapper.code(ResponseCode.ARGS_ERROR.statusCode());
        }
        User user = request.getUser();
        RedisLock lock = RedisLock.getRedisLock("noteComment:" + user.getUuid() + ":" + noteId);
        ResponseMapper ret = lock.lockGracefully(() -> {
            boolean isSendMsg = false;
            Note ar = this.noteDao.getByUuid(noteId);
            if (ar == null) {
                return mapper.code(ResponseCode.ARGS_ERROR.statusCode());
            }
            NoteComment co = new NoteComment();
            co.setNoteId(noteId)
                    .setReplyerId(user.getUuid())
                    .setContent(content)
                    .setAuthorId(ar.getUserId())
                    .setUuid(IdGenerator.uuid());
            if (this.noteCommentDao.insert(co) > 0) {
                this.noteAttrDao.updateByAddition(new NoteAttr()
                        .setNoteId(noteId)
                        .setCommentNum(1));
                // 别人评论的
                if (!co.getAuthorId().equals(co.getReplyerId())) {
                    // 消息推送
                    isSendMsg = true;
                }
            }
            Map<String, String> map = new HashMap<>(8);
            map.put("authorId", ar.getUserId());
            map.put("replyerId", user.getUuid());
            map.put("replyerName", user.getNickname());
            map.put("replyerAvatar", user.getAvatar());
            map.put("content", content);
            map.put("createDate", TimeUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm"));
            if (isSendMsg) {
                try {
                    this.messageService.sendMsgEvent(new Message()
                            .setSenderId(user.getUuid())
                            .setReceiverId(ar.getUserId())
                            .setType(MsgType.NEWS.statusCode())
                            .setBizId(noteId)
                            .setBizType(BizType.NOTE.statusCode())
                            .setBizAction(BizAction.COMMENT.statusCode())
                            .setContent(content)
                            .setReply(null));
                } catch (Exception e) {
                    // do nothing
                }
            }
            return mapper.data(map);
        });
        if (ret == null) {
            return mapper.code(ResponseCode.FAILED.statusCode());
        }
        return ret;

    }

    @Override
    public ResponseMapper reply(TraceRequest request, String commentId, String replyContent) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        if (!request.isLogin()) {
            return mapper.code(ResponseCode.LOGIN_INVALIDATE.statusCode());
        }
        if (StringUtil.isEmpty(replyContent)) {
            return mapper.code(ResponseCode.ARGS_ERROR.statusCode());
        }
        User user = request.getUser();
        NoteComment comment = this.noteCommentDao.getByUuid(commentId);
        if (comment == null) {
            return mapper.code(ResponseCode.NO_DATA.statusCode());
        }
        NoteComment reply = new NoteComment();
        reply.setNoteId(comment.getNoteId())
                .setAuthorId(comment.getAuthorId())
                .setContent(replyContent)
                .setParentId(comment.getUuid())
                .setParentReplyerId(comment.getReplyerId())
                .setReplyerId(user.getUuid())
                .setUuid(IdGenerator.uuid());
        if (this.noteCommentDao.insert(reply) > 0) {
            // 不能是自己回复自己
            if (!user.getUuid().equals(comment.getReplyerId())) {
                this.messageService.sendMsgEvent(new Message()
                        .setSenderId(user.getUuid())
                        .setReceiverId(null)
                        .setType(MsgType.NEWS.statusCode())
                        .setBizId(reply.getUuid())
                        .setBizType(BizType.USER.statusCode())
                        .setBizAction(BizAction.REPLY.statusCode())
                        .setContent(replyContent)
                        .setReply(null));
            }
        }
        return mapper;
    }

    @Override
    public ResponseMapper comments(TraceRequest request, String noteId) {
        int pageNum = request.getHeader().getPageNum();
        int pageSize = request.getHeader().getPageSize();
        pageSize = pageSize > 32 ? 32 : pageSize;

        Page<?> page = PageHelper.startPage(pageNum, pageSize);
        List<NoteCommentVo> list = this.noteCommentDao.findList(noteId);
        if (list.isEmpty()) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.NO_DATA.statusCode());
        }
        List<Map<String, Object>> total = this.handleCommentsList(list, request.getUser(), request.isLogin());
        return ResponseMapper.createMapper()
                .count(page.getTotal())
                .data(total);
    }

    private List<Map<String, Object>> handleCommentsList(final List<NoteCommentVo> list,
            final User user, final boolean isLogin) {
        int size = list.size();
        // 处理数据
        Map<String, Object> map = null;
        List<Map<String, Object>> total = new ArrayList<>(size);
        for (NoteCommentVo a : list) {
            map = MapleUtil.wrap(a)
                    .rename("uuid", "commentId")
                    .stick("createDate", TimeUtils.format(a.getCreateDate(), "yyyy-MM-dd HH:mm"))
                    .stick("isLike", "0")
                    .map();
            total.add(map);
        }
        return total;
    }

    @Override
    public ResponseMapper userComments(TraceRequest request, String userId) {
        if (!request.isLogin()) {
            return ResponseMapper.createMapper().code(ResponseCode.LOGIN_INVALIDATE.statusCode());
        }
        int pageNum = request.getHeader().getPageNum();
        int pageSize = request.getHeader().getPageSize();
        pageSize = pageSize > 32 ? 32 : pageSize;

        Page<?> page = PageHelper.startPage(pageNum, pageSize);
        CommonQuery query = new CommonQuery().setReplyerId(userId);
        List<NoteComment> list = this.noteCommentDao.findByList(query);
        if (list.isEmpty()) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.NO_DATA.statusCode());
        }
        List<Map<String, Object>> total = this.handleUserCommentsList(list);
        return ResponseMapper.createMapper()
                .count(page.getTotal())
                .data(total);
    }

    private List<Map<String, Object>> handleUserCommentsList(final List<NoteComment> list) {
        int size = list.size();
        // 处理数据
        Map<String, Object> map = null;
        List<Map<String, Object>> total = new ArrayList<>(size);
        List<String> noteIds = new ArrayList<>();
        List<String> parentCommentIds = new ArrayList<>();
        List<String> parentReplyerIds = new ArrayList<>();
        for (NoteComment a : list) {
            noteIds.add(a.getNoteId());
            if (StringUtil.isNotEmpty(a.getParentId())) {
                parentCommentIds.add(a.getParentId());
                parentReplyerIds.add(a.getParentReplyerId());
            }
        }
        CommonQuery query = new CommonQuery().setBizIds(noteIds);
        List<Note> notes = this.noteDao.findByList(query);
        Map<String, Note> arMap = new HashMap<>(notes.size());
        notes.forEach(a -> {
            arMap.put(a.getUuid(), a);
        });
        Map<String, UserVo> parentReplyerMap = new HashMap<>(parentReplyerIds.size());
        Map<String, NoteComment> coMap = new HashMap<>(parentReplyerIds.size());
        if (!parentReplyerIds.isEmpty()) {
            List<UserVo> uservos = this.userService.findVoByUuid(parentReplyerIds);
            uservos.forEach(a -> {
                parentReplyerMap.put(a.getUserId(), a);
            });
            query = new CommonQuery();
            query.setCommentIds(parentCommentIds);
            List<NoteComment> comments = this.noteCommentDao.findByList(query);
            comments.forEach(a -> {
                coMap.put(a.getUuid(), a);
            });
        }

        for (NoteComment a : list) {
            map = MapleUtil.wrap()
                    .stick("commentId", a.getUuid())
                    .stick("createDate", TimeUtils.format(a.getCreateDate(), "yyyy-MM-dd HH:mm"))
                    .stick("replyerName", "")
                    .stick("parentReplyerId", a.getParentReplyerId())
                    .stick("content", a.getContent())
                    .map();
            if (!StringUtil.isEmpty(a.getParentId())) {
                NoteComment co = coMap.get(a.getParentId());
                if (co != null) {
                    map.put("parentContent", co.getContent());
                } else {
                    map.put("parentContent", "已删除");
                }
                map.put("parentReplyerName", parentReplyerMap.get(a.getParentReplyerId()).getNickname());
            }
            total.add(map);
        }
        return total;
    }

    @Override
    public ResponseMapper readDestination(TraceRequest request, String noteId) {
        if (!request.isLogin()) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.LOGIN_INVALIDATE.statusCode());
        }
        Note note = this.noteDao.getByUuid(noteId);
        if (note == null) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.NO_DATA.statusCode());
        }
        NoteDestination t = new NoteDestination();
        t.setNoteId(noteId);
        NoteDestination dest = this.noteDestinationDao.get(t);
        if (dest == null) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.NO_DATA.statusCode());
        }
        if (!dest.getReader().equals(request.getUser().getUuid())) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.NO_DATA.statusCode());
        }
        dest.setReadTime(new Date());
        dest.setState(1);
        this.noteDestinationDao.update(dest);
        try {
            this.messageService.sendMsgEvent(new Message()
                    .setReceiverId(note.getUserId())
                    .setType(MsgType.NOTICE.statusCode())
                    .setBizId(noteId)
                    .setBizType(BizType.NOTE.statusCode())
                    .setBizAction(BizAction.NONE.statusCode())
                    .setContent("您的纸条已经被阅读")
                    .setReply(null));
        } catch (Exception e) {
            // do nothing
        }
        return ResponseMapper.createMapper();
    }

    @Override
    public ResponseMapper addLike(TraceRequest request, String noteId) {
        // 没登录 或失效
        if (!request.isLogin()) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.LOGIN_INVALIDATE.statusCode());
        }
        if (this.noteDao.isExist(noteId) < 1) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.ARGS_ERROR.statusCode());
        }
        NoteLike t = new NoteLike()
                .setNoteId(noteId)
                .setUserId(request.getUser().getUuid());
        this.addNoteLikeNum(t);
        return ResponseMapper.createMapper();
    }

    // 这里不用事务 因为不需要太在乎百分百正确
    private void addNoteLikeNum(NoteLike a) {
        NoteLike like = this.likeDao.get(a);
        // 已经点过
        if (like != null) {
            a.setStatus(-like.getStatus());
            a.setNum(like.getNum() + 1);
            this.likeDao.update(a);
        } else {
            a.setUuid(IdGenerator.uuid());
            this.likeDao.insert(a.setStatus(1).setNum(1));
        }
        // 点赞+1 取消-1
        this.noteAttrDao.updateByAddition(new NoteAttr()
                .setNoteId(a.getNoteId())
                .setLikeNum(a.getStatus()));
    }

    @Override
    public ResponseMapper removeNote(TraceRequest request, String noteId) {
        if (!request.isLogin()) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.LOGIN_INVALIDATE.statusCode())
                    .message("登录失效,请重新登录");
        }
        Note note = this.noteDao.getByUuid(noteId);
        if (note == null) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.FAILED.statusCode());
        }
        if (!note.getUserId().equals(request.getUser().getUuid())) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.ARGS_ERROR.statusCode());
        }
        Note t = new Note();
        t.setUuid(noteId);
        this.noteDao.delete(t);
        return ResponseMapper.createMapper();
    }
}