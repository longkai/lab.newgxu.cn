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

import cn.newgxu.lab.apps.notty.entity.AuthorizedUser;
import cn.newgxu.lab.apps.notty.entity.Notice;
import cn.newgxu.lab.apps.notty.repository.AuthDao;
import cn.newgxu.lab.apps.notty.repository.NoticeDao;
import cn.newgxu.lab.apps.notty.service.NoticeService;
import cn.newgxu.lab.core.util.Assert;
import cn.newgxu.lab.core.util.SQLUtils;
import cn.newgxu.lab.core.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import static cn.newgxu.lab.apps.notty.Notty.*;

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
		Assert.notNull(getMessage(USER_NOT_NULL), info.getAuthor());
		Assert.notEmpty(getMessage(TITLE_NOT_NULL), info.getTitle());
		Assert.notEmpty(getMessage(CONTENT_NOT_NULL), info.getContent());
//		文件名和路径绝对不能是空串
		if (TextUtils.isEmpty(info.getDocName(), true)) {
				throw new IllegalArgumentException(getMessage(FILE_NAME_NOT_NULL));
		}
		if (TextUtils.isEmpty(info.getDocUrl(), true)) {
				throw new IllegalArgumentException(getMessage(FILE_URL_NOT_NULL));
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
		throw new UnsupportedOperationException(getMessage(NOT_SUPPORT));
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
		L.info("notices：{} modified successfully！", notice.getTitle());
		return notice;
	}

	@Override
	public Notice find(long pk) {
		Notice n = noticeDao.find(pk);
		Assert.notNull(getMessage(NOT_FOUND), n);
		return n;
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public Notice view(long pk) {
		Notice notice = find(pk);
		notice.setClickTimes(notice.getClickTimes() + 1);
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
		String from = MessageFormat.format("{0} as N JOIN {1} as U ON N.uid=U.id", NoticeDao.TABLE, AuthDao.TABLE);
		return noticeDao.query(null, from, "N.blocked=0 AND N.id<" + lastId, null, null, "id DESC", "" + count);
	}

	@Override
	public List<Notice> listByUser(AuthorizedUser au, int count) {
		String from = MessageFormat.format("{0} as N JOIN {1} as U ON N.uid=U.id", NoticeDao.TABLE, AuthDao.TABLE);
		return noticeDao.query(null, from, "N.blocked=0 AND N.uid=" + au.getId(), null, null, "id DESC", "" + count);
	}

	@Override
	public List<Notice> moreByUser(AuthorizedUser au, long lastId,
			int count) {
		String from = MessageFormat.format("{0} as N JOIN {1} as U ON N.uid=U.id", NoticeDao.TABLE, AuthDao.TABLE);
		String where = String.format("N.blocked=0 AND N.id<%d AND N.uid=%d", lastId, au.getId());
		return noticeDao.query(null, from, where, null, null, "id DESC", "" + count);
	}

	@Override
	public List<Notice> listNewer(long lastId, int count) {
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

		noticeDao.update(SQLUtils
					.set(new String[]{"blocked"}, new Boolean[]{blocked}),
				 "id=" + i.getId());

		L.info("notice: {} block? -> {} successfully!", i.getTitle(), blocked);
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
		Assert.notNull(getMessage(NOT_FOUND), i);

		if (!i.getAuthor().equals(notice.getAuthor())) {
			throw new SecurityException(getMessage(NO_PERMISSION));
		}
	}

	@Override
	public long newerCount(long pk) {
		return noticeDao.count(null, "id>" + pk);
	}

	private void checkRange(int count) {
		if (count < 1 || count > R.getInt(MAX_NOTICES_COUNT.name())) {
			throw new IllegalArgumentException(getMessage(NOTICES_ARG_ERROR));
		}
	}

}