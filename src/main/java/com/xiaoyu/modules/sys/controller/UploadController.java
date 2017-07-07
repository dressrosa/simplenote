/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.sys.controller;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.firewall.FirewalledRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSON;
import com.xiaoyu.common.base.ResponseMapper;
import com.xiaoyu.common.base.ResultConstant;
import com.xiaoyu.common.utils.ImgUtils;
import com.xiaoyu.modules.biz.user.service.IUserService;

/**
 * @author xiaoyu 2016年3月28日
 * @param <E>
 */
@RestController
public class UploadController {

	@Autowired
	private IUserService userService;

	/**
	 * jQuery-File-Upload-9.12.1上传插件 暂时只是单张上传
	 * 
	 * @author xiaoyu
	 * @param initialRequest
	 * @param response
	 * @return
	 * @time 2016年3月29日下午3:01:04
	 */
	@RequestMapping(value = "api/v1/upload/images", method = RequestMethod.POST)
	public String upload(FirewalledRequest initialRequest, HttpServletResponse response) {
		HttpServletRequest request = (HttpServletRequest) initialRequest.getRequest();
		ResponseMapper mapper = ResponseMapper.createMapper();
		Iterator<Part> iter = null;
		Collection<Part> collection = null;
		try {
			collection = request.getParts();
			if (collection.size() > 1) {// 只准上传一张
				return null;
			}
			iter = collection.iterator();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ServletException e1) {
			e1.printStackTrace();
		}
		// Map<String, Object> map = null;
		// List<FileEntity> list = Lists.newArrayList();
		// List<String> list = Lists.newArrayList();
		// while (iter.hasNext()) {
		// Part p = iter.next();
		// path = ImgUtils.saveImgToTencentOss(p);
		// list.add(path);
		// // FileEntity f = new FileEntity();
		// // f.setUrl(path);
		// // f.setName(p.getName());
		// // list.add(f);
		// }

		System.out.println("userid" + request.getHeader("userId"));
		while (iter.hasNext()) {
			Part p = iter.next();
			String result = ImgUtils.saveImgToTencentOss(p);
			System.out.println(result);
			Map<String, Object> map = (Map<String, Object>) JSON.parse(result);
			if (map.get("code").equals(0)) {
				Map<String, String> urlMap = (Map<String, String>) map.get("data");
				String path = urlMap.get("source_url");
				this.userService.editUser(request, request.getHeader("userId"), path, 0);
			}
			System.out.println(JSON.toJSONString(map));
		}
		return mapper.setCode(ResultConstant.ARGS_ERROR).getResultJson();

	}

}
