/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.simplenote.modules.controller;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.firewall.FirewalledRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.xiaoyu.simplenote.common.base.ResponseCode;
import com.xiaoyu.simplenote.common.base.ResponseMapper;
import com.xiaoyu.simplenote.common.request.TraceRequest;
import com.xiaoyu.simplenote.common.util.ImgUtils;
import com.xiaoyu.simplenote.common.util.Utils;
import com.xiaoyu.simplenote.modules.biz.user.service.api.IUserService;

/**
 * @author xiaoyu 2016年3月28日
 */
@RestController
public class UploadController {

    private final static Logger LOG = LoggerFactory.getLogger(UploadController.class);

    @Autowired
    private IUserService userService;

    @Autowired
    private ImgUtils imgUtils;

    /**
     * jQuery-File-Upload-9.12.1上传插件 暂时只是单张上传
     * 
     * @author xiaoyu
     * @param initialRequest
     * @param response
     * @return
     * @time 2016年3月29日下午3:01:04
     */
    @RequestMapping(value = "api/v1/upload/avatar", method = RequestMethod.POST)
    public String uploadAvatar(FirewalledRequest initialRequest, HttpServletResponse response) {
        return this.upload(initialRequest, response, 0);
    }

    @RequestMapping(value = "api/v1/upload/background", method = RequestMethod.POST)
    public String uploadBg(FirewalledRequest initialRequest, HttpServletResponse response) {
        return this.upload(initialRequest, response, 4);
    }

    @SuppressWarnings("unchecked")
    private String upload(FirewalledRequest initialRequest, HttpServletResponse response, int bizType) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        final HttpServletRequest request = (HttpServletRequest) initialRequest.getRequest();
        TraceRequest req = Utils.getTraceRequest(request);
        if (!req.isLogin()) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.LOGIN_INVALIDATE.statusCode())
                    .resultJson();
        }
        Iterator<Part> iter = null;
        Collection<Part> collection = null;
        try {
            collection = request.getParts();
            // 只准上传一张
            if (collection.size() > 1) {
                return mapper.code(ResponseCode.ARGS_ERROR.statusCode()).resultJson();
            }
            iter = collection.iterator();

        } catch (IOException e1) {
            LOG.error(e1.toString());
        } catch (ServletException e2) {
            LOG.error(e2.toString());
        }
        if (iter == null) {
            return mapper.code(ResponseCode.ARGS_ERROR.statusCode()).resultJson();
        }
        while (iter.hasNext()) {
            String result = imgUtils.saveImgToTencentOss(iter.next());
            Map<String, Object> map = (Map<String, Object>) JSON.parse(result);
            String path = null;
            if (map.get("code").equals(0)) {
                Map<String, String> urlMap = (Map<String, String>) map.get("data");
                path = urlMap.get("source_url");
                this.userService.editUser(req, path, bizType);
                return mapper.data(path).resultJson();
            }
        }
        return mapper.code(ResponseCode.ARGS_ERROR.statusCode()).resultJson();
    }
}
