/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */ 
package com.xiaoyu.modules.biz.user.service;

import java.io.IOException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xiaoyu.common.base.BaseService;
import com.xiaoyu.common.utils.IdGenerator;
import com.xiaoyu.common.utils.ImgUtils;
import com.xiaoyu.modules.biz.user.dao.UserDao;
import com.xiaoyu.modules.biz.user.dao.UserRecordDao;
import com.xiaoyu.modules.biz.user.entity.User;
import com.xiaoyu.modules.biz.user.entity.UserRecord;
/**
 * @author xiaoyu
 *2016年3月16日
 */
@Service
@Transactional(readOnly=true)
public class UserService extends BaseService<UserDao,User> {

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private UserRecordDao userRecordDao;
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

	public User getForLogin(User user) {
		User u = this.userDao.getForLogin(user);
		if(u == null) {
			return user;
		}
		return u;
	}

	/**保存登录记录
	 *@author xiaoyu
	 *@param record
	 *@return
	 *@time 2016年4月12日上午10:29:29
	 */
	@Transactional(readOnly=false)
	public int saveUserRecord(UserRecord record) {
		Date date = new Date();
		record.setId(IdGenerator.uuid());
		record.setUpdateDate(date);
		record.setCreateDate(date);
		return this.userRecordDao.insert(record);
	}
}
