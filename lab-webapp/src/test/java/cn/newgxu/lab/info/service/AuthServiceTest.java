/*
 * Copyright 2001-2013 newgxu.cn the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.newgxu.lab.info.service;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import cn.newgxu.lab.info.config.Config;
import cn.newgxu.lab.info.entity.AuthorizedUser;

/**
 * 
 * @author longkai
 * @email im.longkai@gmail.com
 * @since 2013-3-29
 * @version 0.1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/main/webapp/WEB-INF/spring/spring.xml")
@Transactional
public class AuthServiceTest {

	@Inject
	private AuthService authService;
	
	private AuthorizedUser au1;
	private AuthorizedUser au2;
	private AuthorizedUser au3;
	
	@Before
	public void init() {
		au1 = new AuthorizedUser();
		au1.setAccount("im.longkai");
		au1.setPassword("123456");
		au1.setOrg("广西大学雨无声网站");
		au1.setAuthorizedName("广西大学雨无声网站");
		
		au2 = new AuthorizedUser();
		au2.setOrg("广西大学xxx");
		au2.setAuthorizedName("xxx组织");
		
		au3 = new AuthorizedUser();
	}
	
	@Test
	public void testCreate() {
		assertThat(au1.getId() == 0L, is(true));
		authService.create(au1);
		assertThat(au1.getId() != 0L, is(true));
	}
	
//	测试自动生成账户
	@Test
	public void testCreate2() {
		assertThat(au2.getId() == 0L, is(true));
		authService.create(au2);
		assertThat(au2.getId() != 0L, is(true));
	}
	
//	测试某些字段为空的账户
	@Test(expected = RuntimeException.class)
	public void testCreate3() {
		assertThat(au3.getId() == 0L, is(true));
		authService.create(au3);
	}
	
	@Test
	public void testUpdate() {
		testCreate();
		au1.setPassword("654321");
		authService.update(au1);
		assertThat(authService.find(au1.getId()).getPassword(), is("c33367701511b4f6020ec61ded352059"));
	}

	@Test
	public void testBlock() {
		testCreate();
		assertThat(au1.isBlocked(), is(false));
		authService.block(au1);
		assertThat(au1.isBlocked(), is(true));
	}

	@Test
	public void testFind() {
		testCreate();
		assertThat(authService.find(au1.getId()).getAuthorizedName(), is(au1.getAuthorizedName()));
	}

	@Test
	public void testLogin() {
		testCreate();
		assertThat(authService.login(au1.getAccount(), "123456"), notNullValue());
	}
	
//	测试错误账号密码登陆
	@Test(expected = RuntimeException.class)
	public void testLogin2() {
		testCreate();
		assertThat(authService.login(au1.getAccount(), au1.getPassword()), notNullValue());
	}

	@Test
	public void testTotal() {
		assertThat(authService.total(), is(0L));
		testCreate();
		testCreate2();
		assertThat(authService.total(), is(2L));
	}

	@Test
	public void testExists() {
		assertThat(authService.exists(au1.getAccount()), is(false));
		testCreate();
		assertThat(authService.exists(au1.getAccount()), is(true));
	}

	@Test
	public void testList() {
		testCreate();
		testCreate2();
		assertThat(authService.list(0, Config.DEFAULT_USER_LIST_COUNT).size(), is(2));
	}

}
