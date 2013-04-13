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
import cn.newgxu.lab.info.entity.Notice;

/**
 * 信息发布平台的服务接口。
 * 
 * @author longkai
 * @email im.longkai@gmail.com
 * @since 2013-3-28
 * @version 0.1
 */
public interface NoticeService {

	void create(Notice info);

	void delete(Notice info);

	Notice update(Notice info);

	Notice find(long pk);
	
	/** 区别于find，这个会增加1的点击率 */
	Notice view(long pk);

	long total();

	List<Notice> latest();
	
	List<Notice> more(long lastId, int count);
	
	List<Notice> listByUser(AuthorizedUser au, int count);
	
	List<Notice> moreByUser(AuthorizedUser au, long lastId, int count);
	
	List<Notice> listNewer(long lastId, int count);
	
	/**
	 * 屏蔽信息。
	 * @param info 欲屏蔽或者解蔽的信息对象
	 * @param blocked 你懂的
	 */
	Notice block(Notice info, boolean blocked);

	/**
	 * 是否有更新，判断是否有比传过来的参数更大的主键 
	 * @param pk 客户端上最新的主键
	 * @return true or false
	 */
	int newerCount(long pk);

}
