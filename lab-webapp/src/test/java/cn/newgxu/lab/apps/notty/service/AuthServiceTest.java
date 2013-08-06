/*
 * The MIT License (MIT)
 * Copyright (c) 2013 longkai(龙凯)
 */
package cn.newgxu.lab.apps.notty.service;

import cn.newgxu.lab.apps.notty.entity.AuthorizedUser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.AfterTransaction;

import javax.inject.Inject;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author longkai
 * @email im.longkai@gmail.com
 * @since 13-7-31
 * @version 0.1.0.13-7-31
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring-test.xml")
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
//@Transactional
public class AuthServiceTest {

	private static Logger L = LoggerFactory.getLogger(AuthServiceTest.class);

	private static int TOTAL = 8;

	@Inject
	private AuthService authService;

	private AuthorizedUser _u;

	private long id;

	@Before
	public void setUp() throws Exception {
		_u = new AuthorizedUser();
		_u.setOrg("test org");
		_u.setAuthorizedName("test name");
		_u.setContact("10086");
		_u.setAbout("test about");
		_u.setAccount("test account");
		_u.setPassword("test password");

		id = (long) (Math.random() * TOTAL) + 1;
	}

	@After
	public void tearDown() throws Exception {
//		do nothing now...
		System.err.println("done.");
	}

@AfterTransaction
	public void xxx() {
		System.out.println(123);
	}

	@Test
//	@Rollback()
	@DirtiesContext
	public void testCreate() throws Exception {
		assertEquals(_u.getId(), 0L);
		authService.create(_u, _u.getPassword());
		assertTrue(_u.getId() > 0L);
	}

	@Test
//	@Rollback
	@DirtiesContext()
	public void testResetPassword() throws Exception {
		AuthorizedUser user = authService.find(id);
		if (user.isBlocked()) {
			return;
		}
		String newPwd = "_newpwd";
//		all the string params password are plain text.
		authService.resetPassword(user, newPwd, newPwd, null);
		AuthorizedUser u = authService.login(user.getAccount(), newPwd, null);
		assertNotNull(u);
	}

	@Test
//	@Rollback
	@DirtiesContext
	public void testUpdate() throws Exception {
		AuthorizedUser user = authService.find(id);
		String newContact = "10086";
		user.setContact(newContact);
		authService.update(user, "123456");
		assertEquals(newContact, authService.find(id).getContact());
	}

	@Test
//	@Rollback
	@DirtiesContext
	public void testToggleBlock() throws Exception {
		AuthorizedUser user = authService.find(id);
		if (user.isBlocked()) {
			authService.toggleBlock(user);
			assertEquals(false, authService.find(id).isBlocked());
		} else {
			authService.toggleBlock(user);
			assertEquals(true, authService.find(id).isBlocked());
		}
	}

	@Test
	public void testFind() throws Exception {
		AuthorizedUser user = authService.find(id);
		L.debug("u: {}", user);
		assertNotNull(user);
	}

	@Test
//	@Rollback
	@DirtiesContext
	public void testLogin() throws Exception {
		AuthorizedUser user = authService.find(id);
		if (user.isBlocked()) {
			return;
		}
		AuthorizedUser u = authService.login(user.getAccount(), "123456", null);
		assertNotNull(u);
	}

	@Test
	public void testTotal() throws Exception {
		long total = authService.total();
		assertEquals(TOTAL, total);
	}


	@Test
	public void testList() throws Exception {
		L.debug("id={}", id);
		List<AuthorizedUser> users = authService.users(AuthService.LATEST, (int)id);
		for (AuthorizedUser u : users) {
			L.debug("uid: {}, name: {}", u.getId(), u.getAuthorizedName());
		}

		users = authService.users(AuthService.BLOCKED, (int) id);
		for (AuthorizedUser u : users) {
			L.debug("uid: {}, name: {}", u.getId(), u.getAuthorizedName());
		}

		users = authService.users(AuthService.ALL, (int) id);
		for (AuthorizedUser u : users) {
			L.debug("uid: {}, name: {}", u.getId(), u.getAuthorizedName());
		}
	}

	@Test
	public void testList2() throws Exception {
		L.debug("id={}", id);
		List<AuthorizedUser> users = authService.users(AuthService.LATEST, (int) id, true, id);
		for (AuthorizedUser u : users) {
			L.debug("uid: {}, name: {}", u.getId(), u.getAuthorizedName());
		}
		users = authService.users(AuthService.LATEST, (int) id, false, id);
		for (AuthorizedUser u : users) {
			L.debug("uid: {}, name: {}", u.getId(), u.getAuthorizedName());
		}

		users = authService.users(AuthService.BLOCKED, (int) id, true, id);
		for (AuthorizedUser u : users) {
			L.debug("uid: {}, name: {}", u.getId(), u.getAuthorizedName());
		}

		users = authService.users(AuthService.BLOCKED, (int) id, false, id);
		for (AuthorizedUser u : users) {
			L.debug("uid: {}, name: {}", u.getId(), u.getAuthorizedName());
		}

		users = authService.users(AuthService.ALL, (int) id, true, id);
		for (AuthorizedUser u : users) {
			L.debug("uid: {}, name: {}", u.getId(), u.getAuthorizedName());
		}

		users = authService.users(AuthService.ALL, (int) id, false, id);
		for (AuthorizedUser u : users) {
			L.debug("uid: {}, name: {}", u.getId(), u.getAuthorizedName());
		}
	}

	@Test
	public void testSync() throws Exception {
		AuthorizedUser user = authService.find(id);
		L.debug("id:{}, last timestdamp:{}", id, user.getJoinDate().getTime());
		List<AuthorizedUser> users = authService.sync(user.getJoinDate().getTime(), (int) id);
		for (AuthorizedUser u : users) {
			L.debug("u:{}", u);
		}

	}
}
