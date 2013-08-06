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
package cn.newgxu.lab.apps.notty.service;

import java.util.List;

import cn.newgxu.lab.apps.notty.entity.AuthorizedUser;

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

	/** 查询最新的用户列表列表（不包括屏蔽的） */
	int LATEST = 1;

	/** 只查看被屏蔽用户列表 */
	int BLOCKED = 2;

	/** 所有用户的列表（包含屏蔽和未被屏蔽的）*/
	int ALL = 3;

	/** 默认按照注册时间降序 */
	String DEFAULT_ORDER_BY = "id DESC";


	void create(AuthorizedUser user, String confirmedPassword);
	
	/** 修改密码 */
	void resetPassword(AuthorizedUser user, String newPassword, String confirmPassword, String oldPassword);
	
	/** 修改个人信息 */
	void update(AuthorizedUser user, String plainPassword);

	/** 将一个用户账号冻结掉。*/
	void toggleBlock(AuthorizedUser user);
	
	AuthorizedUser find(long pk);
	
	AuthorizedUser login(String account, String password, String ip);
	
	long total();

//	List<AuthorizedUser> latest(int count);
//
//	List<AuthorizedUser> more(long lastUid, int count);
//
//	/** 查看通过授权的用户 */
//	List<AuthorizedUser> authed();

	List<AuthorizedUser> users(int type, int count);

	List<AuthorizedUser> users(int type, int count, boolean append, long offset);

	List<AuthorizedUser> sync(long lastTimestamp, int count);
	
}
