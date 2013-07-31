/*
 * Copyright (c) 2001-2013 newgxu.cn <the original author or authors>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package cn.newgxu.lab.core.repository;

import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 通用的数据访问接口，包含简单的增删改查列表和总记录数方法。
 * 
 * @author longkai
 * @email im.longkai@gmail.com
 * @since 2013-3-28
 * @version 0.1
 */
public interface CommonDao<T> {

	/**
	 * 将一个实体持久化。
	 * @param entity
	 */
	void persist(T entity);
	
	/**
	 * 将一个持久化的实体处于游离化。
	 * @param pk 主键
	 */
	void remove(Serializable pk);
	
	/**
	 * 将持久化的实体与运行时的实体合并，更新是也。
	 * @param entity
	 */
	void merge(T entity);
	
	/**
	 * 通过主键查询一个实体。
	 * @param pk 序列化类型的主键
	 * @return 查找到的实体对象
	 */
	T find(Serializable pk);
	
	/**
	 * 实体类的记录总数。
	 * @return 记录总数
	 */
	long size(@Param("query") String query);

	/**
	 * 自定义更新语句
	 * @param update update sql
	 * @return 更新的记录数
	 */
	int update(@Param("update") String update);

	/**
	 * 自定义询一个实体
	 * @param query sql
	 * @return
	 */
	T one(@Param("query") String query);

	/**
	 * 自定义列表查询.
	 * @param query sql
	 * @return results
	 */
	List<T> list(@Param("query") String query);
	
}
