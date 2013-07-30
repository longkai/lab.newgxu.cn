/*
 * The MIT License (MIT)
 * Copyright (c) 2013 longkai(龙凯)
 */
package cn.newgxu.lab.repository;

import cn.newgxu.lab.info.entity.AuthorizedUser;
import cn.newgxu.lab.info.repository.AuthDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * @author longkai
 * @version 0.1.0
 * @date 13-7-30
 * @email im.longkai@gmail.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/config/spring.xml")
@Transactional
public class AuthdaoTest {

	@Inject
	private AuthDao authDao;

	@Test
	public void testFind() {
		AuthorizedUser user = authDao.find(1);
		System.out.println(user);
	}

}
