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
import cn.newgxu.lab.apps.notty.entity.Notice;

/**
 * 信息发布平台的服务接口。
 * 
 * @author longkai
 * @email im.longkai@gmail.com
 * @since 2013-3-28
 * @version 0.1
 */
public interface NoticeService {

//	int LATEST = 1;
//	int AUTHOR = 2;

	void create(Notice notice);

	void delete(Notice notice);

	void update(Notice notice, long uid);

	Notice find(long pk);
	
	/** 区别于find，这个会增加1的点击率 */
	Notice view(long pk);

	long total();

	/**
	 * 屏蔽屏蔽或者解蔽信息。
	 * @param nid 欲屏蔽或者解蔽的信息对象
	 * @param uid
	 */
	Notice toggleBlock(long nid, long uid);

	/**
	 * 是否有更新，判断是否有比传过来的参数更大的主键 
	 * @param pk 客户端上最新的主键
	 * @return number of the lastest notices
	 */
	long newerCount(long pk);

	List<Notice> latest(int count);

	List<Notice> latest(long uid, int count);

	List<Notice> notices(long offset, boolean append, int count);

	List<Notice> notices(long uid, long offset, boolean append, int count);

	List<Notice> sync(long lastTimestamp, int count);

	// only return title and id!
	List<Notice> search(String keywords, int count);

}
