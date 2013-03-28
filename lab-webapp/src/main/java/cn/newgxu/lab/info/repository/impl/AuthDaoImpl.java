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

import javax.persistence.NoResultException;

import org.springframework.stereotype.Repository;

import cn.newgxu.lab.core.repository.impl.AbstractCommonDaoImpl;
import cn.newgxu.lab.info.entity.AuthorizedUser;
import cn.newgxu.lab.info.repository.AuthDao;

/**
 * 认证用户数据访问接口实现。
 * 
 * @author longkai
 * @email im.longkai@gmail.com
 * @since 2013-3-28
 * @version 0.1
 */
@Repository
public class AuthDaoImpl extends AbstractCommonDaoImpl<AuthorizedUser> implements AuthDao {

	@Override
	public AuthorizedUser find(Serializable pk) {
		return em.find(AuthorizedUser.class, pk);
	}

	@Override
	public long size() {
		return size(AuthorizedUser.class);
	}

	@Override
	public List<AuthorizedUser> list(int offset, int number) {
		return executeQuery("FROM AuthorizedUser au", AuthorizedUser.class, offset, number);
	}

	@Override
	public AuthorizedUser find(String account, String password) {
		return em.createNamedQuery("AuthorizedUser.login", AuthorizedUser.class)
				.setParameter("account", account)
				.setParameter("password", password)
				.getSingleResult();
	}

	@Override
	public boolean has(String account) {
		L.info("查询账号:{} 是否存在！", account);
		AuthorizedUser user = null;
		try {
			user = em.createNamedQuery("AuthorizedUser.account", AuthorizedUser.class)
					.setParameter("account", account)
					.getSingleResult();
		} catch (NoResultException e) {
//			nice, 我们就需要这个异常！
			return false;
		} catch (Exception e) {
			L.error("查询账号存在异常", e);
			return true;
		}
		return user == null ? false : true;
	}

}
