/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.common.base;

import java.util.List;

/**
 * @author xiaoyu 2016年3月22日
 */
public interface BaseDao<T extends BaseEntity> {

	/**
	 * 基本CRUD
	 * 
	 * @author xiaoyu
	 * @param t
	 * @return
	 * @time 2016年3月22日下午2:41:14
	 */
	public int insert(T t);

	public int update(T t);

	public int delete(T t);

	public T get(T t);

	public T getById(String id);

	public int isExist(T t);

	public int isExist(String id);

	public List<T> findByList(T t);
}
