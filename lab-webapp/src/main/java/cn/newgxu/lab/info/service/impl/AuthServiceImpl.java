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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.newgxu.lab.core.util.Assert;
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

	/** 检测两次密码输入是否相符 */
	private void checkPassword(String p1, String p2) {
		// 检测一次就好
		Assert.hasLength("用户密码不能为空或者少于" + Config.MIN_PASSWORD_LENGTH + "位！",
				p1, Config.MIN_PASSWORD_LENGTH);
		if (!p1.equals(p2)) {
			throw new IllegalArgumentException("两次输入密码不一致！");
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void create(AuthorizedUser user, String _pwd) {
		Assert.notEmpty("组织或者单位不能为空！", user.getOrg());
		Assert.notEmpty("显示名称不能为空！", user.getAuthorizedName());

		// 设置密码
		checkPassword(user.getPassword(), _pwd);
		user.setPassword(Encryptor.MD5(Config.PASSWORD_PRIVATE_KEY + _pwd));
		// 设置账号
		Assert.hasLength("账号长度必须大于" + Config.MIN_ACCOUNT_LENGTH + "位！",
				user.getAccount(), Config.MIN_ACCOUNT_LENGTH);
		if (authDao.has(user.getAccount())) {
			throw new RuntimeException("账号已经存在！");
		}

		user.setJoinTime(new Date());
//		一开始认证的时候，我们只会冻结它，等到他正式申请的时候我们在为他解蔽- -
		user.setBlocked(true);
		authDao.persist(user);

		L.info("用户：{} 注册成功！id：{}，账号：{}， org：{}", user.getAuthorizedName(),
				user.getId(), user.getAccount(), user.getOrg());
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public AuthorizedUser resetPassword(AuthorizedUser user, String _pwd) {
		checkPassword(user.getPassword(), _pwd);

		user.setPassword(Encryptor.MD5(Config.PASSWORD_PRIVATE_KEY + _pwd));
		authDao.merge(user);
		L.info("认证用户：{} 修改个人密码成功！", user.getAuthorizedName());
		return user;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public AuthorizedUser update(AuthorizedUser au) {
		au.setAbout(au.getAbout());
		au.setContact(au.getContact());
		authDao.merge(au);
		L.info("认证用户：{} 修改个人信息成功！", au.getAuthorizedName());
		return au;
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
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public AuthorizedUser login(String account, String password, String ip) {
		L.info("用户:{} 登录", account);
		AuthorizedUser user = null;
		try {
			user = authDao.find(account,
					Encryptor.MD5(Config.PASSWORD_PRIVATE_KEY + password));
		} catch (Exception e) {
			L.error("用户登录异常！", e);
			throw new RuntimeException("用户名密码错误！", e);
		}
		if (user.isBlocked()) {
			throw new RuntimeException("对不起，您还没有正式向雨无声申请认证，账号现在还无法使用！请联系雨无声！");
		}
		if (ip != null) {
			user.setLastLoginIP(ip);
			user.setLastLoginTime(new Date());
			authDao.merge(user);
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
	public List<AuthorizedUser> latest() {
		return authDao.list("AuthorizedUser.list_latest", null, 0, Config.DEFAULT_USERS_COUNT);
	}

	@Override
	public List<AuthorizedUser> more(long lastUid, int count) {
		this.checkRange(count);
		Map<String, Object> param = new HashMap<String, Object>(1);
		param.put("last_id", lastUid);
		return authDao.list("AuthorizedUser.list_more", param, 0, count);
	}
	
	/** 检查列表请求数目是否越界 */
	private void checkRange(int count) {
		if (count < 1) {
			throw new IllegalArgumentException("请求列表数目不能为负数！");
		} else if (count > Config.MAX_USERS_COUNT) {
			throw new IllegalArgumentException(
					"您请求列表数目超过限制，最大条数为 " + Config.MAX_USERS_COUNT);
		}
	}

	@Override
	public List<AuthorizedUser> blocked() {
		return authDao.list("AuthorizedUser.list_blocked", null, 0, Config.MAX_USERS_COUNT);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void auth(long uid) {
		AuthorizedUser user = authDao.find(uid);
		Assert.notNull("对不起，该用户不存在！", user);
		user.setBlocked(false);
		authDao.merge(user);
	}
	
}
