/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */ 
package com.xiaoyu.modules.biz.article.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xiaoyu.common.base.BaseService;
import com.xiaoyu.modules.biz.article.dao.ArticleDao;
import com.xiaoyu.modules.biz.article.entity.Article;
import com.xiaoyu.modules.biz.user.dao.UserDao;
import com.xiaoyu.modules.biz.user.entity.User;

/**
 * @author xiaoyu
 *2016年3月29日
 */
@Service
@Transactional(readOnly=true)
public class ArticleService extends BaseService<ArticleDao,Article>{

	@Autowired
	private UserDao userDao;
	
	@Override
	public Article get(Article t) {
		Article a =  super.get(t);
		User u = new User();
		u.setId(a.getUserId());
		u = this.userDao.get(u);
		a.setUser(u);
		return a;
	}

	
	
}
