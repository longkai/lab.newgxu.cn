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
import cn.newgxu.lab.info.entity.Information;

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
public class InfoServiceTest {

	@Inject
	private InfoService infoService;
	
	@Inject
	private AuthService authService;
	
	private AuthorizedUser au;
	
	private Information info;
	private String _pwd;
	
	@Before
	public void init() {
		_pwd = "123456";
		
		au = new AuthorizedUser();
		au.setAccount("longkai");
		au.setPassword("123456");
		au.setAuthorizedName("广西大学雨无声网站");
		au.setOrg("广西大学雨无声网站");
//		初始化一个测试用户，并使之处于持久化状态
		authService.create(au, _pwd);
		
//		初始化一条信息记录
		info = new Information();
		info.setUser(au);
		info.setTitle("雨无声实验室开放啦！");
		info.setContent("雨无声实验室开放啦！");
//		TODO: 处理好文件上传的问题
		info.setDocUrls("http://lab.newgxu.cn/info");
	}
	
	@Test
	public void testCreate() {
		assertThat(info.getId() == 0L, is(true));
		infoService.create(info);
		assertThat(info.getId() != 0L, is(true));
	}
	
//	用户字段为空时的异常
	@Test(expected = RuntimeException.class)
	public void testCreate2() {
		assertThat(info.getId() == 0L, is(true));
		info.setUser(null);
		infoService.create(info);
	}
	
//	某些字段为空时的异常
	@Test(expected = RuntimeException.class)
	public void testCreate3() {
		assertThat(info.getId() == 0L, is(true));
		info.setTitle("");
		infoService.create(info);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testDelete() {
		infoService.delete(info);
	}

	@Test
	public void testUpdate() {
		testCreate();
		String newTitle = "信息发布平台成立啦！";
		assertThat(info.getTitle().equals(newTitle), is(false));
		
		info.setTitle(newTitle);
		info = infoService.update(info);
		assertThat(info.getTitle().equals(newTitle), is(true));
	}

	@Test
	public void testFind() {
		testCreate();
		assertThat(infoService.find(info.getId()).getTitle(), is(info.getTitle()));
	}
	
	@Test
	public void testView() {
		testCreate();
		assertThat(info.getClickTimes(), is(0L));
		infoService.view(info.getId());
		infoService.view(info.getId());
		assertThat(info.getClickTimes(), is(2L));
	}

	@Test
	public void testTotal() {
		testCreate();
		assertThat(infoService.total(), is(1L));
	}

	@Test
	public void testList() {
		testCreate();
		assertThat(infoService.list(0, Config.DEFAULT_INFO_LIST_COUNT).size(), is(1));
	}

	@Test
	public void testBlock() {
		testCreate();
		assertThat(info.isBlocked(), is(false));
		info.setBlocked(true);
		infoService.block(info);
		assertThat(info.isBlocked(), is(true));
	}

	@Test
	public void testIsLatest() {
//		只要能够测试出服务器存在比客户端大的id键即可
		long localId = 0L;
		assertThat(infoService.newerCount(localId), is(0));
		testCreate();
		assertThat(infoService.newerCount(localId), is(1));
	}

}
