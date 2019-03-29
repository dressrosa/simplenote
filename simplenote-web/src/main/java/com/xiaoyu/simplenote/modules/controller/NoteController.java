package com.xiaoyu.simplenote.modules.controller;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaoyu.simplenote.common.base.ResponseCode;
import com.xiaoyu.simplenote.common.base.ResponseMapper;
import com.xiaoyu.simplenote.common.request.TraceRequest;
import com.xiaoyu.simplenote.common.util.ImgUtils;
import com.xiaoyu.simplenote.common.util.Utils;
import com.xiaoyu.simplenote.common.utils.StringUtil;
import com.xiaoyu.simplenote.modules.biz.note.entity.Note;
import com.xiaoyu.simplenote.modules.biz.note.service.api.INoteService;

/**
 * @author hongyu
 * @date 2019-02
 * @description
 */
@RestController
public class NoteController {

    @Autowired
    private INoteService noteService;

    @RequestMapping(value = "api/v1/note/squareList", method = RequestMethod.GET)
    public String squareList(HttpServletRequest request) {
        TraceRequest req = Utils.getTraceRequest(request);
        return this.noteService.squareList(req).resultJson();
    }

    @RequestMapping(value = "api/v1/note/listOfUser", method = RequestMethod.GET)
    public String listOfUser(HttpServletRequest request, String userId) {
        TraceRequest req = Utils.getTraceRequest(request);
        return this.noteService.listOfUser(req, userId).resultJson();
    }

    @RequestMapping(value = "api/v1/note/left", method = RequestMethod.POST)
    public String leftOneNote(HttpServletRequest request, @RequestBody JSONObject json) {
        TraceRequest req = Utils.getTraceRequest(request);
        if (!req.isLogin()) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.LOGIN_INVALIDATE.statusCode()).resultJson();
        }
        if (!json.containsKey("content")) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.ARGS_ERROR.statusCode()).resultJson();
        }
        String content = json.getString("content");
        if (StringUtil.isEmpty(content)) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.ARGS_ERROR.statusCode()).resultJson();
        }
        JSONArray files = json.getJSONArray("files");
        StringBuilder sb = new StringBuilder();
        if (!files.isEmpty()) {
            Iterator<Object> iter = files.iterator();
            while (iter.hasNext()) {
                if (sb.length() > 0) {
                    sb.append(";");
                }
                String f = (String) iter.next();
                if (StringUtil.isNotBlank(f)) {
                    if (f.startsWith("data:image/jpeg;base64,")) {
                        f = f.substring(23);
                    } else if (f.startsWith("data:image/png;base64,")) {
                        f = f.substring(22);
                    }
                    byte[] bytes = Base64.decodeBase64(f);
                    String url = ImgUtils.saveImgToTencentOss(bytes);
                    sb.append(url);
                }
            }
        }
        Note note = new Note()
                .setContent(content)
                .setLocation("");
        return this.noteService.addNote(req, note, sb.toString()).resultJson();
    }

    // public static boolean GenerateImage(String imgStr) { //
    // 对字节数组字符串进行Base64解码并生成图片
    // if(imgStr.startsWith("data:image/jpeg;base64,")) {
    // imgStr =imgStr.substring(23);
    // }else if(imgStr.startsWith("data:image/png;base64,")) {
    // imgStr =imgStr.substring(22);
    // }
    // if (imgStr == null) // 图像数据为空
    // return false;
    // try {
    // // Base64解码
    // byte[] b = Base64.decodeBase64(imgStr);
    // for (int i = 0; i < b.length; ++i) {
    // if (b[i] < 0) {// 调整异常数据
    // b[i] += 256;
    // }
    // }
    // // 生成jpeg图片
    // String imgFilePath = "/Users/hongyu/new.jpg";// 新生成的图片
    // OutputStream out = new FileOutputStream(imgFilePath);
    // out.write(b);
    // out.flush();
    // out.close();
    // return true;
    // } catch (Exception e) {
    // return false;
    // }
    // }

    @RequestMapping(value = "api/v1/note/read", method = RequestMethod.POST)
    public String readNote(HttpServletRequest request, @RequestBody JSONObject json) {
        String noteId = json.getString("noteId");
        TraceRequest req = Utils.getTraceRequest(request);
        return this.noteService.readDestination(req, noteId).resultJson();
    }

    @RequestMapping(value = "api/v1/note/{note}/comment", method = RequestMethod.POST)
    public String comment(HttpServletRequest request, @PathVariable String note,
            @RequestBody JSONObject json) {
        if (!json.containsKey("content")) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.ARGS_ERROR.statusCode()).resultJson();
        }
        TraceRequest req = Utils.getTraceRequest(request);
        if (!req.isLogin()) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.LOGIN_INVALIDATE.statusCode())
                    .resultJson();
        }
        return this.noteService.comment(req, note, json.getString("content")).resultJson();
    }

    @RequestMapping(value = "api/v1/note/like", method = RequestMethod.POST)
    public String like(HttpServletRequest request, @RequestBody JSONObject json) {
        if (!json.containsKey("noteId")) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.ARGS_ERROR.statusCode()).resultJson();
        }
        TraceRequest req = Utils.getTraceRequest(request);
        return this.noteService.addLike(req, json.getString("noteId")).resultJson();
    }

    @RequestMapping(value = "api/v1/note/{note}/comments", method = RequestMethod.GET)
    public String comments(HttpServletRequest request, @PathVariable String note) {
        TraceRequest req = Utils.getTraceRequest(request);
        return this.noteService.comments(req, note).resultJson();
    }

    @RequestMapping(value = "api/v1/note/remove", method = RequestMethod.GET)
    public String removeNote(HttpServletRequest request, @RequestBody JSONObject json) {
        if (!json.containsKey("noteId")) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.ARGS_ERROR.statusCode()).resultJson();
        }
        TraceRequest req = Utils.getTraceRequest(request);
        if (!req.isLogin()) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.LOGIN_INVALIDATE.statusCode())
                    .resultJson();
        }
        return this.noteService.removeNote(req, json.getString("noteId")).resultJson();
    }
}
