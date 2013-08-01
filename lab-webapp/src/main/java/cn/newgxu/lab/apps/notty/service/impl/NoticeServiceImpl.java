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
package cn.newgxu.lab.apps.notty.service.impl;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import cn.newgxu.lab.core.util.SQLUtils;
import cn.newgxu.lab.apps.notty.repository.AuthDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.newgxu.lab.core.util.Assert;
import cn.newgxu.lab.apps.notty.config.Config;
import cn.newgxu.lab.apps.notty.entity.AuthorizedUser;
import cn.newgxu.lab.apps.notty.entity.Notice;
import cn.newgxu.lab.apps.notty.repository.NoticeDao;
import cn.newgxu.lab.apps.notty.service.NoticeService;

import javax.inject.Inject;

/**
 * 信息发布对外服务接口的实现。
 * 
 * @author longkai
 * @email im.longkai@gmail.com
 * @since 2013-3-29
 * @version 0.1
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Throwable.class)
public class NoticeServiceImpl implements NoticeService {

	private static Logger L = LoggerFactory.getLogger(NoticeServiceImpl.class);
	
	@Inject
	private NoticeDao noticeDao;
	
	/** 简单地验证信息是否合法 */
	private void validate(Notice info) {
		Assert.notNull("认证用户不能为空！", info.getAuthor());
		Assert.notEmpty("信息标题不能为空！", info.getTitle());
		Assert.notEmpty("信息内容不能为空！", info.getContent());
//		文件名和路径绝对不能是空串
		if (info.getDocName() != null) {
			if (info.getDocName().trim().equals("")) {
				throw new IllegalArgumentException("文件名不能是空的啊亲！");
			}
		}
		if (info.getDocUrl() != null) {
			if (info.getDocUrl().trim().equals("")) {
				throw new IllegalArgumentException("文件路径不能是空的啊亲！");
			}
		}
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void create(Notice notice) {
		validate(notice);
		
		notice.setAddDate(new Date());
		notice.setLastModifiedDate(notice.getAddDate());
		noticeDao.insert(notice);
		L.info("info_user：{} post new notice: {} successfully!",
				notice.getAuthor().getAuthorizedName(), notice.getTitle());
	}

	@Override
	public void delete(Notice notice) {
		throw new UnsupportedOperationException("对不起，暂不支持此操作！");
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public Notice update(Notice notice) {
		validate(notice);
		
		Notice i = noticeDao.find(notice.getId());
		
		assertBelong(notice, i);
		
		i.setLastModifiedDate(new Date());
		i.setTitle(notice.getTitle());
		i.setContent(notice.getContent());
//		文件上传处理
		if (notice.getDocName() != null) {
			i.setDocName(notice.getDocName());
		}
		if (notice.getDocUrl() != null) {
			i.setDocUrl(notice.getDocUrl());
		}
		
//		noticeDao.merge(i);
		noticeDao.update(SQLUtils
					.set(new String[]{"last_modified_date", "title", "content", "doc_name", "doc_url"},
							new Object[]{i.getLastModifiedDate(), i.getTitle(), i.getContent(),
									i.getDocName(), i.getDocUrl()}),
					"id=" + i.getId());
		L.info("信息：{} 修改成功！", notice.getTitle());
		return notice;
	}

	@Override
	public Notice find(long pk) {
		Notice n = noticeDao.find(pk);
		Assert.notNull("对不起，您要查看的信息不存在！", n);
		return n;
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public Notice view(long pk) {
		Notice notice = find(pk);
		notice.setClickTimes(notice.getClickTimes() + 1);
//		noticeDao.merge(notice);
		noticeDao.update("click_times=click_times+1", "id=" + pk);
		return notice;
	}

	@Override
	public long total() {
		return noticeDao.count(null, null);
	}

	@Override
	public List<Notice> latest(int count) {
		String from = MessageFormat.format("{0} as N JOIN {1} as U ON N.uid=U.id", NoticeDao.TABLE, AuthDao.TABLE);
		return noticeDao.query(null, from, "N.blocked=0", null, null, "id DESC", "" + count);
	}

	@Override
	public List<Notice> more(long lastId, int count) {
//		this.checkRange(count);
//		Map<String, Object> param = new HashMap<String, Object>();
//		param.put("last_id", lastId);
//		return noticeDao.list("Notice.list_more", param, 0, count);
		String from = MessageFormat.format("{0} as N JOIN {1} as U ON N.uid=U.id", NoticeDao.TABLE, AuthDao.TABLE);
		return noticeDao.query(null, from, "N.blocked=0 AND N.id<" + lastId, null, null, "id DESC", "" + count);
	}

	@Override
	public List<Notice> listByUser(AuthorizedUser au, int count) {
//		this.checkRange(count);
//		Map<String, Object> param = new HashMap<String, Object>(1);
//		param.put("user", au);
//		return noticeDao.list("Notice.list_user_latest", param, 0, count);
		String from = MessageFormat.format("{0} as N JOIN {1} as U ON N.uid=U.id", NoticeDao.TABLE, AuthDao.TABLE);
		return noticeDao.query(null, from, "N.blocked=0 AND N.uid=" + au.getId(), null, null, "id DESC", "" + count);
	}

	@Override
	public List<Notice> moreByUser(AuthorizedUser au, long lastId,
			int count) {
//		this.checkRange(count);
//		Map<String, Object> params = new HashMap<String, Object>(2);
//		params.put("user", au);
//		params.put("last_id", lastId);
//		return noticeDao.list("Notice.list_user_more", params, 0, count);
		String from = MessageFormat.format("{0} as N JOIN {1} as U ON N.uid=U.id", NoticeDao.TABLE, AuthDao.TABLE);
		String where = String.format("N.blocked=0 AND N.id<%d AND N.uid=%d", lastId, au.getId());
		return noticeDao.query(null, from, where, null, null, "id DESC", "" + count);
	}

	@Override
	public List<Notice> listNewer(long lastId, int count) {
//		this.checkRange(count);
//		Map<String, Object> param = new HashMap<String, Object>(1);
//		param.put("last_id", lastId);
//		return noticeDao.list("Notice.list_newer", param, 0, count);
		String from = MessageFormat.format("{0} as N JOIN {1} as U ON N.uid=U.id", NoticeDao.TABLE, AuthDao.TABLE);
		return noticeDao.query(null, from, "N.blocked=0 AND N.id>" + lastId, null, null, null, "" + count);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public Notice block(Notice notice, boolean blocked) {
		Notice i = noticeDao.find(notice.getId());

		assertBelong(notice, i);

		if (blocked) {
			i.setBlocked(true);
			notice.setBlocked(true);
		} else {
			i.setBlocked(false);
			notice.setBlocked(false);
		}

//		noticeDao.merge(i);
		noticeDao.update(SQLUtils
					.set(new String[]{"blocked"}, new Boolean[]{blocked}),
				 "id=" + i.getId());

		L.info("信息：{} 屏蔽?：{}成功！", i.getTitle(), blocked);
		return notice;
	}

	/**
	 * 1，检查数据库是否存在该信息；2，检查信息的发布者是否是同一人
	 * @param notice
	 * @param i
	 * @throws SecurityException
	 */
	private void assertBelong(Notice notice, Notice i)
			throws SecurityException {
		Assert.notNull("对不起，您所访问的信息不存在！", i);

		if (!i.getAuthor().equals(notice.getAuthor())) {
			throw new SecurityException("对不起，这条信息不是您发布的，您无权对其进行操作！");
		}
	}

	@Override
	public long newerCount(long pk) {
		return noticeDao.count(null, "id>" + pk);
	}

	private void checkRange(int count) {
		if (count < 1) {
			throw new IllegalArgumentException("请求的列表数目不能是负数啊亲！");
		} else if (count > Config.MAX_NOTICES_COUNT) {
			throw new IllegalArgumentException("请求的列表数目不能超过 " + Config.MAX_NOTICES_COUNT + " 条！");
		}
	}

}