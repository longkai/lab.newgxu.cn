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

import java.util.List;

import cn.newgxu.lab.info.entity.AuthorizedUser;

/**
 * 认证用户的对外服务接口。
 * 
 * @author longkai
 * @email im.longkai@gmail.com
 * @since 2013-3-28
 * @version 0.1
 */
public interface AuthService {
	
	/** 查询最新的列表 */
	int LIST_LATEST = 1;
	
	/** 用于获取更多用户 */
	int LIST_MORE = 2;
	
	/** 创建用户，假如没有明确账号密码，那么系统就会自动生成唯一的账号与密码。 */
	void create(AuthorizedUser user, String _pwd);
	
	/** 修改密码 */
	AuthorizedUser resetPassword(AuthorizedUser user, String _pwd);
	
	/** 修改个人信息 */
	AuthorizedUser update(AuthorizedUser au);

	/** 将一个用户账号冻结掉。*/
	void block(AuthorizedUser user);
	
	AuthorizedUser find(long pk);
	
	AuthorizedUser login(String account, String password, String ip);
	
	long total();
	
	/** 账号名是否存在 */
	boolean exists(String account);
	
	List<AuthorizedUser> latest();
	
	List<AuthorizedUser> more(long lastUid, int count);
	
}
