/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */ 
package com.xiaoyu.modules.biz.article.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xiaoyu.common.base.BaseService;
import com.xiaoyu.common.utils.IdGenerator;
import com.xiaoyu.common.utils.StringUtils;
import com.xiaoyu.modules.biz.article.dao.ArticleAttrDao;
import com.xiaoyu.modules.biz.article.dao.ArticleDao;
import com.xiaoyu.modules.biz.article.entity.Article;
import com.xiaoyu.modules.biz.article.entity.ArticleAttr;
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
	@Autowired
	private ArticleDao articleDao;
	@Autowired
	private ArticleAttrDao attrDao;
	
	@Override
	public Article get(Article t) {
		Article a =  super.get(t);
		User u = new User();
		u.setId(a.getUserId());
		u = this.userDao.get(u);
		a.setUser(u);
		return a;
	}

	@Override
	public int save(Article t) {
		ArticleAttr attr = new ArticleAttr();
		int temp = 0;
		if(null == t) {
			return temp;
		}
		if(StringUtils.isNotBlank(t.getId())) {
			return temp;
		}
		Date date = new Date();
		t.setId(IdGenerator.uuid());
		t.setCreateDate(date);
		t.setUpdateDate(date);
		try {
			temp = this.articleDao.insert(t);
			attr.setArticleId(t.getId());
			attr.setId(IdGenerator.uuid());
			attr.setCreateDate(date);
			attr.setUpdateDate(date);
			return this.attrDao.insert(attr);			
		}
		catch(RuntimeException e)  {
			throw e;
		}
	}

	
	
}
