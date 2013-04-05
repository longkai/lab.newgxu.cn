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
package cn.newgxu.lab.info.repository.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

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
	public List<AuthorizedUser> list(String query, Map<String, Object> params, int offset, int number) {
		return super.executeQuery(query, AuthorizedUser.class, offset, number, params);
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
