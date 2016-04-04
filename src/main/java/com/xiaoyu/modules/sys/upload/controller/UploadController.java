/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.sys.upload.controller;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.security.web.firewall.FirewalledRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xiaoyu.common.utils.ImgUtils;
import com.xiaoyu.modules.sys.upload.entity.FileEntity;

/**
 * @author xiaoyu 2016年3月28日
 * @param <E>
 */
@Controller
@EnableAutoConfiguration
@RequestMapping("upload")
public class UploadController {
	
	/**jQuery-File-Upload-9.12.1上传插件 暂时只是单张上传
	 *@author xiaoyu
	 *@param initialRequest
	 *@param response
	 *@return
	 *@time 2016年3月29日下午3:01:04
	 */
	@RequestMapping(value = "images", method = RequestMethod.POST)
	@ResponseBody
	public String upload(FirewalledRequest initialRequest,HttpServletResponse response) {
		HttpServletRequest request = (HttpServletRequest) initialRequest
				.getRequest();
		String path = null;
		Iterator<Part> iter = null;
		try {
			Collection<Part> collection= request.getParts();
			if(collection.size() > 1) {//只准上传一张
				return null;
			}
			iter = collection.iterator();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ServletException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Map<String, Object> map =null;
		List<FileEntity> list = Lists.newArrayList();
		while (iter.hasNext()) {
			Part p  =  iter.next();
			path = ImgUtils.saveImg(p);
			FileEntity f=new FileEntity();
			f.setUrl(path);
			f.setName(p.getName());
			list.add(f);
		}
		map = Maps.newHashMap();
		map.put("files", list);
		//System.out.println(JSON.toJSONString(map));
		return JSON.toJSONString(map);
	}

	/**跳转上传页面
	 *@author xiaoyu
	 *@param request
	 *@param response
	 *@return
	 *@time 2016年3月29日下午4:02:17
	 */
	@RequestMapping("goUpload")
	public String goUpload(HttpServletRequest request, HttpServletResponse response) {
		return "common/uploadFile";
	}
}
