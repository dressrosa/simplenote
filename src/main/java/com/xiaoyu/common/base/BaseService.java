/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.common.base;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.xiaoyu.common.utils.IdGenerator;
import com.xiaoyu.common.utils.StringUtils;

/**
 * 基本CRUD操作,坚决不返回null
 * 
 * @author xiaoyu 2016年3月22日
 */
@Transactional(readOnly = true)
public abstract class BaseService<D extends BaseDao<T>, T extends BaseEntity> {

	@Autowired
	private D tDao;

	/**
	 * 获取t,否则原样返回
	 * 
	 * @author xiaoyu
	 * @param t
	 * @return
	 * @time 2016年3月22日下午3:13:24
	 */
	public T get(T t) {
		T temp = null;
		temp = this.tDao.get(t);
		return temp;
	}

	/**
	 * 删除
	 * 
	 * @author xiaoyu
	 * @param t
	 * @return
	 * @time 2016年3月22日下午3:14:41
	 */
	@Transactional(readOnly = false)
	public int delete(T t) {
		return this.tDao.delete(t);
	}

	/**
	 * 查找集合
	 * 
	 * @author xiaoyu
	 * @param t
	 * @return
	 * @time 2016年3月22日下午3:21:49
	 */
	public List<T> findByList(T t) {
		List<T> list = Lists.newArrayList();
		if (null == t) {
			return list;
		}
		list = this.tDao.findByList(t);
		return list;
	}

	/**
	 * 分页查找
	 * 
	 * @author xiaoyu
	 * @param t
	 * @param pageNo
	 * @param pageSize
	 * @return
	 * @time 2016年3月22日下午3:29:14
	 */
	public Page<T> findByPage(T t, int pageNum, int pageSize) {
		Page<T> page = new Page<T>();
		if (null == t) {
			return page;
		}

		PageHelper.startPage(pageNum, pageSize);
		page = (Page<T>) this.tDao.findByList(t);
		return page;
	}

	/**
	 * 更新
	 * 
	 * @author xiaoyu
	 * @param t
	 * @return
	 * @time 2016年3月22日下午3:38:49
	 */
	@Transactional(readOnly = false)
	public int update(T t) {
		int temp = 0;
		if (null == t) {
			return temp;
		}
		Date date = new Date();
		t.setUpdateDate(date);
		temp = this.tDao.update(t);
		return temp;
	}

	/**
	 * 保存
	 * 
	 * @author xiaoyu
	 * @param t
	 * @return
	 * @time 2016年3月22日下午3:39:25
	 */
	@Transactional(readOnly = false)
	public int save(T t) {
		int temp = 0;
		if (null == t) {
			return temp;
		}
		if (StringUtils.isNotBlank(t.getId())) {
			return temp;
		}
		Date date = new Date();
		t.setId(IdGenerator.uuid());
		t.setCreateDate(date);
		t.setUpdateDate(date);
		temp = this.tDao.insert(t);
		return temp;
	}

	/**
	 * 判断是否存在
	 * 
	 * @author xiaoyu
	 * @param t
	 * @return
	 * @time 2016年3月22日下午3:13:11
	 */
	private boolean isExist(T t) {
		if (null == t) {
			return false;
		}
		int count = this.tDao.isExist(t);
		if (count > 0) {
			return true;
		}
		return false;
	}
}
