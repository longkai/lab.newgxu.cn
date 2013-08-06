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
package cn.newgxu.lab.apps.notty.service.impl;

import cn.newgxu.lab.apps.notty.Notty;
import cn.newgxu.lab.core.util.Assert;
import cn.newgxu.lab.core.util.Encryptor;
import cn.newgxu.lab.core.util.SQLUtils;
import cn.newgxu.lab.apps.notty.entity.AuthorizedUser;
import cn.newgxu.lab.apps.notty.repository.AuthDao;
import cn.newgxu.lab.apps.notty.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

/**
 * 认证用户对外服务的实现。
 *
 * @author longkai
 * @version 0.1
 * @email im.longkai@gmail.com
 * @since 2013-3-28
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Throwable.class)
public class AuthServiceImpl implements AuthService {

	private static Logger L = LoggerFactory.getLogger(AuthServiceImpl.class);

	@Inject
	private AuthDao authDao;

	/**
	 * 检测两次密码输入是否相符
	 */
	private void checkPassword(String p1, String p2) {
		// 检测一次就好

		Assert.between(
				Notty.PASSWORD_RANGE,
				p1,
				Notty.MIN_PASSWORD_LENGTH,
				Notty.MAX_PASSWORD_LENGTH);
		if (!p1.equals(p2)) {
			throw new IllegalArgumentException(Notty.PASSWORDS_NOT_EQUALS);
		}
	}

	private long howManyAccount(String account) {
		return authDao.count(null, SQLUtils.where("account=?", new String[]{account}));
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void create(AuthorizedUser user, String confirmedPassword) {
		Assert.notEmpty(Notty.ORG_NOT_NULL, user.getOrg());
		Assert.notEmpty(Notty.AUTHED_NAME_NOT_NULL, user.getAuthorizedName());
		System.out.println(user.getPassword() + "  " + confirmedPassword);
		// 设置密码
		checkPassword(user.getPassword(), confirmedPassword);
		user.setPassword(Encryptor.MD5(Notty.PASSWORD_PRIVATE_KEY + confirmedPassword));
		// 设置账号
		Assert.between(
				Notty.ACCOUNT_RANGE,
				user.getAccount(),
				Notty.MIN_ACCOUNT_LENGTH,
				Notty.MAX_ACCOUNT_LENGTH
		);

		if (howManyAccount(user.getAccount()) > 0) {
			throw new RuntimeException(Notty.ACCOUNT_EXISTS);
		}

		user.setJoinDate(new Date());
//		统一由管理员负责填写和认证，所以省略掉。
		user.setBlocked(false);
		authDao.insert(user);

		L.info("info_user: {} register succellfully! id: {}, account: {}, org: {}", user.getAuthorizedName(),
				user.getId(), user.getAccount(), user.getOrg());
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void resetPassword(AuthorizedUser user, String newPassword, String confirmPassword, String oldPassword) {
//		if old password == null, that' s admin!
		if (oldPassword != null) {
			updatable(user, oldPassword);
		}
//		check new password
		checkPassword(newPassword, confirmPassword);
		user.setPassword(Encryptor.MD5(Notty.PASSWORD_PRIVATE_KEY + newPassword));

		user.setLastModifiedDate(new Date());
		int i = authDao.update(SQLUtils
				.set(new String[]{"password", "last_modified_date"},
						new Object[]{user.getPassword(), user.getLastModifiedDate()})
				, "id=" + user.getId());
		L.info("notice/user: {} successfully modify password. update count: {}",
				user.getAuthorizedName(), i);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void update(AuthorizedUser user, String plainPassword) {
		updatable(user, plainPassword);
		user.setLastModifiedDate(new Date());
		int i = authDao.update(SQLUtils
				.set(new String[]{"about", "contact", "last_modified_date"},
						new Object[]{user.getAbout(), user.getContact(), user.getLastModifiedDate()}),
				"id=" + user.getId());
		L.info("info_user: {} modify self infomation successfully! update count: {}",
				user.getAuthorizedName(), i);
	}

	private boolean updatable(AuthorizedUser user, String plainPassword) {
		boolean equals = Encryptor.MD5(Notty.PASSWORD_PRIVATE_KEY + plainPassword)
				.equals(user.getPassword());
		if (!equals) {
			throw new SecurityException(Notty.OLD_PASSWORD_NOT_MATCHES);
		}
		return equals;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void toggleBlock(AuthorizedUser user) {
		user.setBlocked(!user.isBlocked());
		user.setLastModifiedDate(new Date());
		int i = authDao.update(SQLUtils
				.set(new String[]{"blocked", "last_modified_date"},
						new Object[]{user.isBlocked(), user.getLastModifiedDate()}),
				"id=" + user.getId());
		L.info("info_user: {} blocked: {}. update count: {}", user.getAuthorizedName(), user.isBlocked(), i);
	}

	@Override
	public AuthorizedUser find(long pk) {
		AuthorizedUser user = authDao.find(pk);
		Assert.notNull(Notty.NOT_FOUND, user);
		return user;
	}

	private AuthorizedUser login(String account, String password) {
		return authDao.findOne(null, null,
				SQLUtils.where("account=? AND password=?",
						new String[]{account, password}));
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public AuthorizedUser login(String account, String password, String ip) {
		L.info("info_user: {} try logining...", account);
		AuthorizedUser user = null;
		try {
			user = this.login(
					account,
					Encryptor.MD5(Notty.PASSWORD_PRIVATE_KEY + password));
			if (user == null) {
				throw new RuntimeException(Notty.PASSWORDS_NOT_EQUALS);
			}
		} catch (Exception e) {
			L.error(String.format("app login error with account: %s", account), e);
			throw new RuntimeException(Notty.ACCOUNT_PASSWORD_NOT_MATCHS, e);
		}


		if (user.isBlocked()) {
			throw new RuntimeException(Notty.ACCOUNT_BLOCKED);
		}

		user.setLastLoginIP(ip);
		user.setLastLoginDate(new Date());
		user.setLastModifiedDate(user.getLastLoginDate());
		authDao.update(SQLUtils
				.set(new String[]{"last_login_ip", "last_login_date", "last_modified_date"},
						new Object[]{ip, user.getLastLoginDate(), user.getLastModifiedDate()}),
				"id=" + user.getId());
		return user;
	}

	@Override
	public long total() {
		return authDao.count(null, null);
	}

	/**
	 * 检查列表请求数目是否越界
	 */
	private void checkRange(int count) {
		if (count < 1 || count > Notty.MAX_USERS_COUNT) {
			throw new IllegalArgumentException(Notty.USERS_ARG_ERROR);
		}
	}

	@Override
	public List<AuthorizedUser> users(int type, int count) {
		checkRange(count);
		List<AuthorizedUser> users;
		switch (type) {
			case LATEST:
				users = authDao.query(null, null, "blocked=0", null, null, DEFAULT_ORDER_BY, "" + count);
				break;
			case ALL:
				users = authDao.query(null, null, null, null, null, DEFAULT_ORDER_BY, "" + count);
				break;
			case BLOCKED:
				users = authDao.query(null, null, "blocked=1", null, null, DEFAULT_ORDER_BY, "" + count);
				break;
			default:
				throw new UnsupportedOperationException(Notty.NOT_SUPPORT + "[type=" + type + "]");
		}
		return users;
	}

	@Override
	public List<AuthorizedUser> users(int type, int count, boolean append, long offset) {
		checkRange(count);
		List<AuthorizedUser> users;
		String where = String.format("id%s%d", append ? "<" : ">", offset);
		switch (type) {
			case LATEST:
				users = authDao.query(null, null, "blocked=0 AND " + where, null, null, DEFAULT_ORDER_BY, "" + count);
				break;
			case BLOCKED:
				users = authDao.query(null, null, "blocked=1 AND " + where, null, null, DEFAULT_ORDER_BY, "" + count);
				break;
			case ALL:
				users = authDao.query(null, null, where, null, null, DEFAULT_ORDER_BY, "" + count);
				break;
			default:
				throw new UnsupportedOperationException(Notty.NOT_SUPPORT + "[type=" + type + "]");
		}
		return users;
	}

	@Override
	public List<AuthorizedUser> sync(long lastTimestamp, int count) {
		checkRange(count);
		String where = "last_modified_date>? OR join_date>?";
		Date date = new Date(lastTimestamp);
		return authDao
			.query(null, null,
					SQLUtils.where(where, new Object[]{date, date}),
					 null, null, DEFAULT_ORDER_BY, "" + count);
	}

}
