/*
 * The MIT License (MIT)
 * Copyright (c) 2013 longkai(龙凯)
 */

package cn.newgxu.lab.apps.notty.service;

import cn.newgxu.lab.apps.notty.entity.AuthorizedUser;
import cn.newgxu.lab.apps.notty.entity.Notice;
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

import static org.junit.Assert.*;

/**
 * @author longkai
 * @version 0.1.0.13-8-1
 * @email im.longkai@gmail.com
 * @since 13-8-1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/config/spring-test.xml")
public class NoticeServiceTest {

	private static Logger L = LoggerFactory.getLogger(NoticeServiceTest.class);
	private static int TOTAL = 17;
	private static int TOTAL_USER = 8;

	private int nid;
	private int uid;

	@Inject
	private NoticeService noticeService;

	@Inject
	private AuthService authService;

	private Notice _n;

	@Before
	public void setUp() throws Exception {
		nid = (int) (Math.random() * TOTAL) + 1;
		uid = (int) (Math.random() * TOTAL_USER) + 1;

		_n = new Notice();
		_n.setAuthor(authService.find(uid));
		_n.setTitle("test title");
		_n.setContent("test content");
	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	@Rollback
	public void testCreate() throws Exception {
		noticeService.create(_n);
		assertTrue(_n.getId() > 0L);
	}

	@Test(expected = Exception.class)
	@Rollback
	public void testUnvalid() throws Exception {
		_n.setTitle(null);
		noticeService.create(_n);
	}

	@Test
	public void testDelete() throws Exception {
//      not yet implement
	}

	@Test
	@Rollback
	public void testUpdate() throws Exception {
		Notice n = noticeService.find(nid);
		String newTitle = "___new";
		n.setTitle(newTitle);
		noticeService.update(n);
		assertEquals(newTitle, noticeService.find(nid).getTitle());
	}

	@Test
	public void testFind() throws Exception {
		Notice notice = noticeService.find(nid);
		L.debug("notice: {}", notice);
		assertNotNull(notice);
	}

	@Test(expected = RuntimeException.class)
	public void testNotFound() throws Exception {
		noticeService.find(nid + Integer.MAX_VALUE);
	}

	@Test
	@Rollback
	public void testView() throws Exception {
		Notice n1 = noticeService.find(nid);
		Notice n2 = noticeService.view(nid);
		assertNotNull(n1);
		assertNotNull(n2);
		assertTrue(n1.getClickTimes() == n2.getClickTimes() - 1);
	}

	@Test
	public void testTotal() throws Exception {
		assertEquals(TOTAL, noticeService.total());
	}

	@Test
	public void testLatest() throws Exception {
		List<Notice> notices = noticeService.latest(2);
		assertTrue(notices.get(0).getId() >= TOTAL);
		assertTrue(notices.get(0).getId() > notices.get(1).getId());
	}

	@Test
	public void testMore() throws Exception {
		if (nid <= 2) {
			nid = 3;
		}
		List<Notice> notices = noticeService.more(nid, 2);
		if (notices.size() < 3) {
			return;
		}
		assertTrue(notices.get(0).getId() > notices.get(1).getId());
		assertTrue(notices.get(1).getId() < nid);
	}

	@Test
	public void testListByUser() throws Exception {
		AuthorizedUser u = authService.find(uid);
		List<Notice> notices = noticeService.listByUser(u, 10);
		for (Notice notice : notices) {
			assertTrue(notice.getAuthor().equals(u));
		}
	}

	@Test
	public void testMoreByUser() throws Exception {
		L.debug("nid: {}, uid: {}", nid, uid);
		nid = 1; uid = 4;
		List<Notice> notices = noticeService.moreByUser(authService.find(uid), nid, 5);
		for (int i = 0; i < notices.size(); i++) {
			Notice n = notices.get(i);
			L.debug("n{} id={}", i, n.getId());
			assertTrue(n.getId() < nid);
		}
	}

	@Test
	public void testListNewer() throws Exception {
		List<Notice> notices = noticeService.listNewer(nid, 5);
		for (Notice notice : notices) {
			assertTrue(notice.getId() > nid);
		}
	}

	@Test
	@Rollback
	public void testBlock() throws Exception {
		Notice n = noticeService.find(nid);
		boolean blocked = n.isBlocked();
		noticeService.block(n, !blocked);
		assertTrue(noticeService.find(nid).isBlocked() != blocked);
	}

	@Test
	public void testNewerCount() throws Exception {
		long l = noticeService.newerCount(nid);
		if (nid < noticeService.latest(1).get(0).getId()) {
			assertTrue(l > 0);
		}
	}
}
