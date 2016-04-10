/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */ 
package com.xiaoyu.modules.biz.article.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xiaoyu.common.base.BaseService;
import com.xiaoyu.common.utils.IdGenerator;
import com.xiaoyu.modules.biz.article.dao.ArticleAttrDao;
import com.xiaoyu.modules.biz.article.entity.ArticleAttr;

/**
 * @author xiaoyu
 *2016年4月8日
 */
@Service
@Transactional(readOnly=true)
public class ArticleAttrService extends  BaseService<ArticleAttrDao,ArticleAttr>{

	
	@Autowired
	private ArticleAttrDao attrDao;
	
	/**更新阅读数
	 *@author xiaoyu
	 *@param attr
	 *@return
	 *@time 2016年4月8日下午10:40:39
	 */
	@Transactional(readOnly=false)
	public int updateReadNum(ArticleAttr attr) {
		if(this.attrDao.isExist(attr) ==0) {
			this.save(attr);
		}
		attr = this.attrDao.getForUpdate(attr);//行级锁
		ArticleAttr temp = new ArticleAttr();
		temp.setId(attr.getId());
		temp.setArticleId(attr.getArticleId());
		temp.setReadNum(attr.getReadNum()+1);
		this.attrDao.update(temp);
		return temp.getReadNum();
	}

	
}
