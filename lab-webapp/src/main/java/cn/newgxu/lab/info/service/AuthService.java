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

	/** 创建用户，假如没有明确账号密码，那么系统就会自动生成唯一的账号与密码。 */
	void create(AuthorizedUser user);
	
	/** 目前仅支持修改密码 */
	AuthorizedUser update(AuthorizedUser user);

	/** 将一个用户账号冻结掉。*/
	void block(AuthorizedUser user);
	
	AuthorizedUser find(long pk);
	
	AuthorizedUser login(String account, String password);
	
	long total();
	
	/** 账号名是否存在 */
	boolean exists(String account);
	
	List<AuthorizedUser> list(int NO, int howMany);
	
}
