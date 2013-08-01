/*
 * The MIT License (MIT)
 * Copyright (c) 2013 longkai(龙凯)
 */

package cn.newgxu.lab.apps.notty.repository;

import cn.newgxu.lab.core.util.SQLUtils;
import cn.newgxu.lab.apps.notty.entity.Notice;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author longkai
 * @version 0.1.0.13-7-31
 * @email im.longkai@gmail.com
 * @since 13-7-31
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/config/spring-test.xml")
public class NoticeDaoTest {

	private static Logger L = LoggerFactory.getLogger(NoticeDaoTest.class);

	@Inject
	private NoticeDao noticeDao;

	@Inject
	private AuthDao authDao;

	private Notice n;

	private static int COUNT = 17;

	private int id;

	@Before
	public void setUp() throws Exception {
		n = new Notice();
		n.setTitle("test title");
		n.setAddDate(new Date());
		n.setContent("test content");
		n.setAuthor(authDao.find(1));

		id = (int) (Math.random() * COUNT) + 1;
	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	@Transactional
	public void testPersist() throws Exception {
		noticeDao.insert(n);
		L.debug("n: {}", n);
		assertTrue(n.getId() > 0);
	}

	@Test
	public void testFind() throws Exception {
		Notice n = noticeDao.find(1);
		L.debug("n:{}", n);
		L.debug("u: {}", n.getAuthor());
		L.debug("u: {}", n.getAuthor().getAuthorizedName());
		L.debug("u: {}", n.getAuthor().getContact());
		assertNotNull(n);
	}

	@Test
	@Transactional
	public void testUpdate() throws Exception {
		noticeDao.insert(n);
		L.debug("n:{}", n);
		String newTitle = "new title";
		n.setTitle(newTitle);
		noticeDao.update(SQLUtils.set(new String[]{"title"}, new Object[]{newTitle}), "id=" + n.getId());
		assertEquals(newTitle, noticeDao.find(n.getId()).getTitle());
	}

	@Test
	@Transactional
	public void testRemove() throws Exception {
		noticeDao.insert(n);
		L.debug("n: {}", n);
		noticeDao.delete(null, "id=" + n.getId());
		assertNull(noticeDao.find(n.getId()));
	}

	@Test
	public void testQuery() throws Exception {
		List<Notice> notices = noticeDao.query("*",
				MessageFormat.format("{0} N JOIN {1} U ON N.uid=U.id", NoticeDao.TABLE, AuthDao.TABLE),
				"N.id=" + id, null, null, null, null);
		for (int i = 0; i < notices.size(); i++) {
			Notice notice = notices.get(i);
			L.debug("n{}: {}, u: {}", i, notice, notice.getAuthor().getId());
		}
	}
}
