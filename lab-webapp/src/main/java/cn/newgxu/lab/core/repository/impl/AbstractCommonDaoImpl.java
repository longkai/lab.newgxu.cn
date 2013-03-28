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
package cn.newgxu.lab.core.repository.impl;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import cn.newgxu.lab.core.repository.CommonDao;

/**
 * 抽象的通用数据访问接口的实现，同时提供了一些实用dao方法。
 * 
 * @author longkai
 * @email im.longkai@gmail.com
 * @since 2013-3-28
 * @version 0.1
 */
public abstract class AbstractCommonDaoImpl<T> implements CommonDao<T> {

	/** 共用的日志对象 */
	protected static final Logger	L	= LoggerFactory.getLogger(AbstractCommonDaoImpl.class);

	@PersistenceContext
	protected EntityManager			em;

	@Override
	public void persist(T entity) {
		Assert.notNull(entity, "实体类对象不能为空！");
		em.persist(entity);
	}

	@Override
	public void remove(T entity) {
		Assert.notNull(entity, "实体类对象不能为空！");
		em.remove(entity);
	}

	@Override
	public void merge(T entity) {
		Assert.notNull(entity, "实体类对象不能为空！");
		em.merge(entity);
	}

	/**
	 * 通过实体类类型返回该实体类的总记录数。
	 * @param clazz 实体类
	 * @return 该实体类的总记录数
	 */
	protected long size(Class<?> clazz) {
		return em.createQuery("SELECT count(*) FROM " + clazz.getSimpleName(), Long.class)
					.getSingleResult();
	}
	
	/**
	 * 查询，只返回一个对象，支持<b style="color: red;"> ? </b>格式来作为占位符。
	 * @param hql hql
	 * @param type 查询的实体类型
	 * @param objects 参数, 为null表示无参数
	 * @return 单个对象
	 */
	protected T executeQuery(String hql, Class<T> clazz, Object...objects) {
		TypedQuery<T> query = em.createQuery(hql, clazz);
		if (objects != null) {
			for (int i = 0; i < objects.length; i++) {
				query.setParameter(i + 1, objects[i]);
			}
		}
		return query.getSingleResult();
	}
	
	/**
	 * 查询，只返回一个对象，支持<b style="color: red;"> :xxx </b>格式来作为参数占位符。
	 * @param hql 预处理hql
	 * @param type 查询的实体类型
	 * @param objects 参数map, 不能为空
	 * @return 单个对象
	 */
	protected T executeQuery(String hql, Class<T> clazz, Map<String, Object> params) {
		TypedQuery<T> query = em.createQuery(hql, clazz);
		for (String name : params.keySet()) {
			query.setParameter(name, params.get(name));
		}
		return query.getSingleResult();
	}

	/**
	 * 查询，返回一个列表，支持<b style="color: red;"> ? </b>格式来作为参数占位符。
	 * @param hql hql
	 * @param clazz 查询的实体类型
	 * @param offset 偏移量，即，从第几条记录开始取
	 * @param number 抓取数，即，抓取多少条记录
	 * @param objects 参数数组，为null时表示无参数
	 * @return 对象列表
	 */
	protected List<T> executeQuery(String hql, Class<T> clazz, int offset, int number, Object...objects) {
		offset = rangeCheck(offset, clazz);
		TypedQuery<T> query = em.createQuery(hql, clazz);
		if (objects != null) {
			for (int i = 0; i < objects.length; i++) {
				query.setParameter(i + 1, objects[i]);
			}
		}
		return query.setFirstResult(offset).setMaxResults(number).getResultList();
	}
	
	/**
	 * 查询，返回一个列表，支持<b style="color: red;"> :xxx </b>格式来作为参数占位符。
	 * @param hql hql
	 * @param clazz 查询的实体类型
	 * @param offset 偏移量，即，从第几条记录开始取
	 * @param number 抓取数，即，抓取多少条记录
	 * @param params 参数map，不能为空
	 * @return 对象列表
	 */
	protected List<T> executeQuery(String hql, Class<T> clazz, int offset, int number, Map<String, Object> params) {
		offset = rangeCheck(offset, clazz);
		TypedQuery<T> query = em.createQuery(hql, clazz);
		for (String name : params.keySet()) {
			query.setParameter(name, params.get(name));
		}
		return query.setFirstResult(offset).setMaxResults(number).getResultList();
	}
	
	/**
	 * 检测偏移量是否越界。
	 * 
	 * @param offset 偏移量
	 * @param clazz 操作的实体类型
	 * @return 调整后（越界）的偏移量
	 */
	protected int rangeCheck(int offset, Class<?> clazz) {
		if (offset < 0) {
			offset = 0;
			return offset;
		}
		long size = size(clazz);
		if (offset > size) {
			offset = Long.valueOf(size).intValue();
		}
		return offset;
	}

}
