/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */ 
package com.xiaoyu.modules.biz.user.service;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xiaoyu.common.base.BaseService;
import com.xiaoyu.common.utils.ImgUtils;
import com.xiaoyu.modules.biz.user.dao.UserDao;
import com.xiaoyu.modules.biz.user.entity.User;
/**
 * @author xiaoyu
 *2016年3月16日
 */
@Service
@Transactional(readOnly=true)
public class UserService extends BaseService<UserDao,User> {

//	@Transactional(readOnly=false)
//	public int saveUser(User user, HttpServletRequest request) {
//		String path = null;
//		try {
//			 path = ImgUtils.saveImg(user.getImgFile(),request);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		user.setImg(path);
//		return super.save(user);
//	}

	@Transactional(readOnly=false)
	public int uploadImg(User user) {
		String path = null;
		try {
			 path = ImgUtils.upload(user.getImgFile());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		user.setImg(path);
		return super.update(user);
	}

	
}
