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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
@ContextConfiguration("/config/spring-test.xml")
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
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
	}

	@Test
	@Rollback()
	public void testCreate() throws Exception {
		assertEquals(_u.getId(), 0L);
		authService.create(_u, _u.getPassword());
		assertTrue(_u.getId() > 0L);
	}

	@Test
	@Rollback
	public void testResetPassword() throws Exception {
		AuthorizedUser user = authService.find(id);
		String newPwd = "_newpwd";
		user.setPassword(newPwd);
		authService.resetPassword(user, newPwd);
		AuthorizedUser u = authService.login(user.getAccount(), newPwd, null);
		assertNotNull(u);
	}

	@Test
	@Rollback
	public void testUpdate() throws Exception {
		AuthorizedUser user = authService.find(id);
		String newContact = "10086";
		user.setContact(newContact);
		authService.update(user);
		assertEquals(newContact, authService.find(id).getContact());
	}

	@Test
	@Rollback
	public void testToggleBlock() throws Exception {
		AuthorizedUser user = authService.find(id);
		if (user.isBlocked()) {
			authService.toggleBlock(user, false);
			assertEquals(false, authService.find(id).isBlocked());
		} else {
			authService.toggleBlock(user, true);
			assertEquals(true, authService.find(id).isBlocked());
		}
	}

	@Test
	public void testFind() throws Exception {
		AuthorizedUser user = authService.find(id);
		L.debug("u: {}", user.toJsonObject());
		assertNotNull(user);
	}

	@Test
	@Rollback
	public void testLogin() throws Exception {
		AuthorizedUser user = authService.find(id);
		AuthorizedUser u = authService.login(user.getAccount(), "123456", null);
		assertNotNull(u);
	}

	@Test
	public void testTotal() throws Exception {
		long total = authService.total();
		assertEquals(TOTAL, total);
	}

	@Test
	public void testExists() throws Exception {
	    String account = authService.find(id).getAccount();
		assertTrue(authService.exists(account));
	}

	@Test
	public void testLatest() throws Exception {
		List<AuthorizedUser> users = authService.latest((int) id);
		assertEquals(id, users.size());
	}

	@Test
	public void testMore() throws Exception {
		if (id == 1) {
			id++;
		}

		List<AuthorizedUser> users = authService.more(id, 1);
		assertTrue(users.get(0).getId() < id);
	}

	@Test
	public void testAuthed() throws Exception {
		List<AuthorizedUser> users = authService.authed();
		assertTrue(users.size() > 0);
	}
}
