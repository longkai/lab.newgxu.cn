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
package cn.newgxu.lab.info.repository.impl;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Repository;

import cn.newgxu.lab.core.repository.impl.AbstractCommonDaoImpl;
import cn.newgxu.lab.info.entity.Information;
import cn.newgxu.lab.info.repository.InfoDao;

/**
 * 信息发布的数据访问实现。
 * 
 * @author longkai
 * @email im.longkai@gmail.com
 * @since 2013-3-28
 * @version 0.1
 */
@Repository
public class InfoDaoImpl extends AbstractCommonDaoImpl<Information> implements InfoDao {

	@Override
	public Information find(Serializable pk) {
		return super.em.find(Information.class, pk);
	}

	@Override
	public long size() {
		return super.size(Information.class);
	}

	@Override
	public List<Information> list(int offset, int number) {
		return super.executeQuery("FROM Information i", Information.class, offset, number);
	}

	@Override
	public int newerCount(long pk) {
		long l = 0L;
		try {
			l = em.createNamedQuery("Information.has_new", Long.class)
					.setParameter("id", pk)
					.getSingleResult();
		} catch (Exception e) {
			L.error("查询是否有更新时异常！", e);
		}
		return Long.valueOf(l).intValue();
	}

}
