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

import cn.newgxu.lab.apps.notty.Notty;
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

	private static final String DEFAULT_FROM_CLAUSE =
			MessageFormat.format("{0} as N JOIN {1} as U ON N.uid=U.id",
				NoticeDao.TABLE, AuthDao.TABLE);

	private static final String DEFAULT_ORDER_BY = "N.id DESC";
	
	@Inject
	private NoticeDao noticeDao;

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void create(Notice notice) {
		validate(notice);
		
		notice.setAddDate(new Date());
		noticeDao.insert(notice);
		L.info("auth_user：{} post a new notice: {} successfully!",
				notice.getAuthor().getAuthorizedName(), notice.getTitle());
	}

	@Override
	public void delete(Notice notice) {
		throw new UnsupportedOperationException(Notty.NOT_SUPPORT);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void update(Notice notice, long uid) {
		validate(notice);
		assertBelong(notice, uid);

		notice.setLastModifiedDate(new Date());
		noticeDao.update(SQLUtils
				.set(new String[]{"last_modified_date", "title", "content", "doc_name", "doc_url"},
						new Object[]{notice.getLastModifiedDate(), notice.getTitle(), notice.getContent(),
								notice.getDocName(), notice.getDocUrl()}),
				"id=" + notice.getId());
		L.info("notice：{} modified successfully！", notice.getTitle());
	}

	@Override
	public Notice find(long pk) {
		Notice n = noticeDao.find(pk);
		Assert.notNull(Notty.NOT_FOUND, n);
		return n;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public Notice view(long pk) {
		Notice notice = find(pk);
		int i = noticeDao.update("click_times=click_times+1", "id=" + pk);
		if (i > 0)
			notice.setClickTimes(notice.getClickTimes() + 1);
		return notice;
	}

	@Override
	public long total() {
		return noticeDao.count(null, null);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public Notice toggleBlock(long nid, long uid) {
		Notice notice = find(nid);

		assertBelong(notice, uid);

		notice.setBlocked(!notice.isBlocked());

		noticeDao.update(SQLUtils
					.set(new String[]{"blocked"}, new Boolean[]{notice.isBlocked()}),
				 "id=" + notice.getId());

		L.info("notice: {} block? -> {} successfully!", notice.getTitle(), notice.isBlocked());
		return notice;
	}

	@Override
	public long newerCount(long pk) {
		return noticeDao.count(null, "id>" + pk);
	}

	@Override
	public List<Notice> latest(int count) {
		checkRange(count);
		return noticeDao.query(null, DEFAULT_FROM_CLAUSE, "N.blocked=0", null, null, DEFAULT_ORDER_BY, "" + count);
	}

	@Override
	public List<Notice> latest(long uid, int count) {
		checkRange(count);
		return noticeDao.query(null, DEFAULT_FROM_CLAUSE, "U.id=" + uid, null, null, DEFAULT_ORDER_BY, "" + count);
	}

	@Override
	public List<Notice> notices(long offset, boolean append, int count) {
		checkRange(count);
		String where = String.format("N.id%s%d", append ? "<" : ">", offset);
		return noticeDao.query(null, DEFAULT_FROM_CLAUSE, "N.blocked=0 AND " + where,
						null, null, DEFAULT_ORDER_BY, "" + count);
	}

	@Override
	public List<Notice> notices(long uid, long offset, boolean append, int count) {
		checkRange(count);
		String where = String.format("N.id%s%d", append ? "<" : ">", offset);
		return noticeDao.query(null, DEFAULT_FROM_CLAUSE,
				 "N.uid=" + uid + " AND " + where, null, null, DEFAULT_ORDER_BY, "" + count);
	}

	@Override
	public List<Notice> sync(long lastTimestamp, int count) {
		checkRange(count);
		String where = "N.last_modified_date>? OR N.add_date>?";
		Date last = new Date(lastTimestamp);
		return noticeDao.query(null, DEFAULT_FROM_CLAUSE, SQLUtils.where(where, new Object[]{last, last}),
					null, null, DEFAULT_ORDER_BY, "" + count);
	}

	@Override
	public List<Notice> search(String keywords, int count) {
		checkRange(count);
		String where = SQLUtils.like("N.title like '%?%' OR N.content like '%?%'", keywords, keywords);
		return noticeDao.
			query(SQLUtils.columns(new String[]{"N.id", "N.title"}), null, where, null, null, DEFAULT_ORDER_BY, "" + count);
	}

	private void checkRange(int count) {
		if (count < 1 || count > Notty.MAX_NOTICES_COUNT) {
			throw new IllegalArgumentException(Notty.NOTICES_ARG_ERROR);
		}
	}

	/** 简单地验证信息是否合法 */
	private void validate(Notice info) {
		Assert.notNull(Notty.USER_NOT_NULL, info.getAuthor());
		Assert.notEmpty(Notty.TITLE_NOT_NULL, info.getTitle());
		Assert.notEmpty(Notty.CONTENT_NOT_NULL, info.getContent());
//		文件名和路径绝对不能是空串
		if (TextUtils.isEmpty(info.getDocName(), true)) {
//				throw new IllegalArgumentException(getMessage(FILE_NAME_NOT_NULL));
			info.setDocName(null);
		}
		if (TextUtils.isEmpty(info.getDocUrl(), true)) {
//				throw new IllegalArgumentException(getMessage(FILE_URL_NOT_NULL));
			info.setDocUrl(null);
		}
	}

	/**
	 * 1，检查数据库是否存在该信息；2，检查信息的发布者是否是同一人
	 * @param notice
	 * @param uid
	 */
	private void assertBelong(Notice notice, long uid) {
		if (notice.getAuthor().getId() != uid) {
			throw new SecurityException(Notty.NO_PERMISSION);
		}
	}

}