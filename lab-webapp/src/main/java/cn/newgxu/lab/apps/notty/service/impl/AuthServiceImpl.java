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

import cn.newgxu.lab.core.util.Assert;
import cn.newgxu.lab.core.util.Encryptor;
import cn.newgxu.lab.core.util.SQLUtils;
import cn.newgxu.lab.apps.notty.config.Config;
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
 * @email im.longkai@gmail.com
 * @since 2013-3-28
 * @version 0.1
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Throwable.class)
public class AuthServiceImpl implements AuthService {

	private static Logger L	= LoggerFactory.getLogger(AuthServiceImpl.class);

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

	private long howManyAccount(String account) {
		return authDao.count(null, SQLUtils.where("account=?", new String[]{account}));
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
//		if (authDao.howMany(user.getAccount()) != 0) {
//			throw new RuntimeException("账号已经存在！");
//		}

		if (howManyAccount(user.getAccount()) > 0) {
			throw new RuntimeException("账号已经存在！");
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
	public AuthorizedUser resetPassword(AuthorizedUser user, String _pwd) {
		checkPassword(user.getPassword(), _pwd);

		user.setPassword(Encryptor.MD5(Config.PASSWORD_PRIVATE_KEY + _pwd));
		user.setLastModifiedDate(new Date());
//		authDao.update(SQLUtils.
//				update(AuthDao.TABLE,
//						new String[]{"password", "last_modified_date"},
//						new Object[]{user.getPassword(), user.getLastModifiedDate()},
//						"id=" + user.getId(), null));
		int i = authDao.update(SQLUtils
				.set(new String[]{"password", "last_modified_date"},
						new Object[]{user.getPassword(), user.getLastModifiedDate()})
				, "id=" + user.getId());
		L.info("info_user: {} successfully modify password. update count: {}",
				 user.getAuthorizedName(), i);
		return user;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public AuthorizedUser update(AuthorizedUser au) {
		au.setLastModifiedDate(new Date());
//		int i = authDao.update(SQLUtils
//				.update(AuthDao.TABLE,
//						new String[]{"about", "contact", "last_modified_date"},
//						new Object[]{au.getAbout(), au.getContact(), au.getLastModifiedDate()},
//						"id=" + au.getId(), null));
		int i = authDao.update(SQLUtils
				.set(new String[]{"about", "contact", "last_modified_date"},
						new Object[]{au.getAbout(), au.getContact(), au.getLastModifiedDate()}),
				"id=" + au.getId());
		L.info("info_user: {} modify self infomation successfully! update count: {}",
				au.getAuthorizedName(), i);
		return au;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void toggleBlock(AuthorizedUser user, boolean blocked) {
		user.setBlocked(blocked);
		user.setLastModifiedDate(new Date());
//		int i = authDao.update(SQLUtils
//				.update(AuthDao.TABLE,
//						new String[]{"blocked", "last_modified_date"},
//						new Object[]{blocked, user.getLastModifiedDate()},
//						"id=" + user.getId(), null));
		int i = authDao.update(SQLUtils
				.set(new String[]{"blocked", "last_modified_date"},
						new Object[]{blocked, user.getLastModifiedDate()}),
				"id=" + user.getId());
		L.info("info_user: {} blocked: {}. update count: {}", user.getAuthorizedName(), blocked, i);
	}

	@Override
	public AuthorizedUser find(long pk) {
		return authDao.find(pk);
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
			user = this.login(account,
					Encryptor.MD5(Config.PASSWORD_PRIVATE_KEY + password));
			if (user == null) {
				throw new RuntimeException("账号密码不匹配！");
			}
		} catch (Exception e) {
			L.error(String.format("notty login error with account: %s", account), e);
			throw new RuntimeException("账号密码不匹配！", e);
		}


		if (user.isBlocked()) {
			throw new RuntimeException("对不起，您的账号目前处于冻结状态无法使用，请联系雨无声！");
		}

		user.setLastLoginIP(ip);
		user.setLastLoginDate(new Date());
		user.setLastModifiedDate(user.getLastLoginDate());
//		authDao.update(SQLUtils
//				.update(AuthDao.TABLE,
//						new String[]{"last_login_ip", "last_login_date", "last_modified_date"},
//						new Object[]{ip, user.getLastLoginIP(), user.getLastModifiedDate()},
//						"id=" + user.getId(), null));
		authDao.update(SQLUtils
				.set(new String[]{"last_login_ip", "last_login_date", "last_modified_date"},
						new Object[]{ip, user.getLastLoginIP(), user.getLastModifiedDate()}),
				"id=" + user.getId());
		return user;
	}

	@Override
	public long total() {
//		return authDao.count(SQLUtils.selectCount(AuthDao.TABLE, null, null));
		return authDao.count(null, null);
	}

	@Override
	public boolean exists(String account) {
		return this.howManyAccount(account) > 0;
	}

	@Override
	public List<AuthorizedUser> latest(int count) {
//		return authDao.list(SQLUtils
//				.query(AuthDao.TABLE, null,
//						"blocked=?", new Boolean[]{false}, null, null, "id DESC", "" + count));
		return authDao.query(null, null,
				 SQLUtils.where("blocked=?", new Boolean[]{false}),
				  null, null, "id DESC", "" + count);
	}

	@Override
	public List<AuthorizedUser> more(long lastUid, int count) {
//		return authDao.list(SQLUtils
//				.query(AuthDao.TABLE, null,
//						"id<? AND blocked=?", new Object[]{lastUid, false},
//						null, null, "id DESC", "" + count));
		return authDao.query(null, null,
				 SQLUtils.where("id<? AND blocked=?", new Object[]{lastUid, false}),
					null, null, "id DESC", "" + count);
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
	public List<AuthorizedUser> authed() {
//		return authDao.list(SQLUtils
//				.query(AuthDao.TABLE, null, "blocked=?", new Boolean[]{false}, null, null, "id desc", null));
		return authDao.query(null, null,
					SQLUtils.where("blocked=?", new Boolean[]{false}),
					null, null, "id DESC", null);
	}

}