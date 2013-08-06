/*
* The MIT License (MIT)
* Copyright (c) 2013 longkai(龙凯)
*/
package cn.newgxu.lab.apps.notty.repository;

import cn.newgxu.lab.core.util.SQLUtils;
import cn.newgxu.lab.apps.notty.entity.AuthorizedUser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

/**
* @author longkai
* @version 0.1.0
* @date 13-7-30
* @email im.longkai@gmail.com
*/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring-test.xml")
public class AuthdaoTest {

	private static Logger L = LoggerFactory.getLogger(AuthdaoTest.class);

	@Inject
	private AuthDao authDao;

	private AuthorizedUser u;

	@Before
	public void init() {
		u = new AuthorizedUser();
		u.setPassword("123456");
		u.setAccount("longkai-idea");
		u.setAbout("idea");
		u.setAuthorizedName("idea");
		u.setContact("14795633343");
		u.setJoinDate(new Date());
		u.setBlocked(false);
		u.setLastLoginIP("127.0.0.1");
		u.setLastLoginDate(new Date());
		u.setOrg("yws");
	}

	@Test
	public void testFind() {
		AuthorizedUser user = authDao.find(1);
		L.debug("{}", user);
	}

	@Test
//	@Rollback
	@DirtiesContext
	public void testPsersit() throws Exception {
		authDao.insert(u);
		System.out.println(u);
	}

	@Test
//	@Rollback
	@DirtiesContext
	public void testRemove() throws Exception {
		authDao.insert(u);
//		L.debug("{}", u);
//		authDao.remove();
	}

	@Test
	public void testSize() throws Exception {
        long size = authDao.count(null, null);
		L.debug("size: {}", size);
	}

	@Test
	public void testUsers() throws Exception {
//		List<AuthorizedUser> users = authDao.users(1, 5, false);
//		Assert.assertEquals(users.size(), 10);
//		Assert.assertEquals(authDao.users(0, 10, true).size(), 5);
//		List<AuthorizedUser> users = authDao.users(SQLUtils.columns(new String[]{"id", "authed_name"}));
		List<AuthorizedUser> users = authDao.query(null, null, null, null, null, "id desc", "2,1");
		L.debug("size: {}", users.size());
		for (int i = 0; i < users.size(); i++) {
			L.debug("user: {}", users.get(i));
		}
//		AuthorizedUser user = authDao.one(SQLUtils
//				.query("info_users", null, "id=1", null, null, null, null, null));
//		L.debug("u: {}", user);
	}

	@Test
//	@Rollback
	@DirtiesContext
	public void testUpdate() throws Exception {
		authDao.insert(u);
		L.debug("u: {}", u);
//		int i = authDao.update(SQLUtils
//				.update("info_users", new String[]{"authed_name"}, new Object[]{"___update"}, "id=?", new Object[]{u.getId()}));
		int i = authDao.update(SQLUtils
				.set(new String[]{"authed_name"},
						new Object[]{"___update"}),
				"id=" + u.getId());
//		Assert.assertEquals(i, 1);
		L.debug("i:{}", i);
	}

	@Test
	public void testInit() throws Exception {
//		AuthorizedUser user = authDao.login("ma_yun", "123456");
//		L.debug("u:{}", user);
		List<AuthorizedUser> users = authDao.query(null, null, null, null, null, null, null);
		for (int i = 0; i < users.size(); i++) {
			L.debug("u: {}", users.get(i));
		}
	}
}
