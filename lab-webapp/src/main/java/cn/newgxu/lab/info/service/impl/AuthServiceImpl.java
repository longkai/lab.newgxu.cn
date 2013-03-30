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
package cn.newgxu.lab.info.service.impl;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.newgxu.lab.core.util.Assert;
import cn.newgxu.lab.core.util.DateTime;
import cn.newgxu.lab.core.util.Encryptor;
import cn.newgxu.lab.info.config.Config;
import cn.newgxu.lab.info.entity.AuthorizedUser;
import cn.newgxu.lab.info.repository.AuthDao;
import cn.newgxu.lab.info.service.AuthService;

/**
 * 认证用户对外服务的实现。
 * 
 * @author longkai
 * @email im.longkai@gmail.com
 * @since 2013-3-28
 * @version 0.1
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Throwable.class)
public class AuthServiceImpl implements AuthService {

	private static final Logger	L	= LoggerFactory.getLogger(AuthServiceImpl.class);

	@Inject
	private AuthDao				authDao;

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void create(AuthorizedUser user) {
		Assert.notEmpty("组织或者单位不能为空！", user.getOrg());
		Assert.notEmpty("显示名称不能为空！", user.getAuthorizedName());
		
		// 设置密码
		if (user.getPassword() != null) {
			String plainText = user.getPassword().trim();
			if (plainText.equals("")) {
				throw new RuntimeException("密码不能为空！");
			}
			user.setPassword(Encryptor.MD5(plainText));
		} else {
			user.setPassword(Encryptor.MD5(Config.DEFAULT_PASSWORD));
		}

		Date now = new Date();
		
		// 设置账号
		if (user.getAccount() == null) {
			// 如果是没有指定账号，那么我们就按照时间来，这样肯定不会有重复
			user.setAccount(Config.DEFAULT_ACCOUNT_PREFIX + DateTime.format(now, Config.DEFAULT_ACCOUNT_SUFFIX));
		} else {
//			检查账号名是否存在！
			if (authDao.has(user.getAccount())) {
				throw new RuntimeException("用户名已经存在！");
			}
		}
		
		user.setJoinTime(now);

		authDao.persist(user);

		L.info("用户：{} 注册成功！id：{}，账号：{}， org：{}", user.getAuthorizedName(),
				user.getId(), user.getAccount(), user.getOrg());
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public AuthorizedUser update(AuthorizedUser user) {
		AuthorizedUser u = authDao.find(user.getId());
		u.setPassword(Encryptor.MD5(user.getPassword()));
		authDao.merge(u);
		L.info("用户：{} 修改个人信息成功！", user.getAuthorizedName());
		return u;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void block(AuthorizedUser user) {
		user.setBlocked(true);
		authDao.merge(user);
		L.info("用户：{} 账号被成功冻结！", user.getAuthorizedName());
	}

	@Override
	public AuthorizedUser find(long pk) {
		return authDao.find(pk);
	}

	@Override
	public AuthorizedUser login(String account, String password) {
		L.info("用户:{} 登录", account);
		AuthorizedUser user = null;
		try {
			user = authDao.find(account, Encryptor.MD5(password));
		} catch (Exception e) {
			L.error("用户登录异常！", e);
			throw new RuntimeException("用户登录异常！", e);
		}
		return user;
	}

	@Override
	public long total() {
		return authDao.size();
	}

	@Override
	public boolean exists(String account) {
		return authDao.has(account);
	}

	@Override
	public List<AuthorizedUser> list(int offset, int howMany) {
		if (howMany <= 0) {
			howMany = Config.DEFAULT_USER_LIST_COUNT;
		}
		return authDao.list(offset, howMany);
	}

}
