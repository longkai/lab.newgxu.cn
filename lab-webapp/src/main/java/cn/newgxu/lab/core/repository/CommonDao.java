/*
 * Copyright 2001-2013 newgxu.cn the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.newgxu.lab.core.repository;

import java.io.Serializable;
import java.util.List;

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
	 * @param entity
	 */
	void remove(T entity);
	
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
	long size();
	
	/**
	 * 获取实体类对象的列表。
	 * @param offset 偏移量，即，从第几条记录开始取
	 * @param number 抓取数，即，抓取多少条记录
	 * @return 实体类对象列表
	 */
	List<T> list(int offset, int number);
	
}
