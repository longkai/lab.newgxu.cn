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
package cn.newgxu.lab.apps.notty.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.json.JsonArray;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import cn.newgxu.lab.apps.notty.Notty;
import cn.newgxu.lab.core.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import cn.newgxu.lab.core.util.Assert;
import cn.newgxu.lab.apps.notty.entity.AuthorizedUser;
import cn.newgxu.lab.apps.notty.entity.Notice;
import cn.newgxu.lab.apps.notty.service.AuthService;
import cn.newgxu.lab.apps.notty.service.NoticeService;

import static cn.newgxu.lab.core.common.ViewConstants.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * 信息发布平台关于信息发布查看修改等的控制器。
 *
 * @author longkai
 * @version 0.1
 * @email im.longkai@gmail.com
 * @since 2013-3-29
 */
@Controller
@Scope("session")
public class NoticeController {

	private static Logger L = LoggerFactory.getLogger(NoticeController.class);

	@Inject
	private NoticeService noticeService;

	@Inject
	private AuthService authService;

	@RequestMapping(value = {"/notice", "/info"}, method = GET)
	public String index(Model model) {
		List<Notice> notices = noticeService.latest(Notty.DEFAULT_NOTICES_COUNT);
		List<AuthorizedUser> users = authService.users(AuthService.LATEST, Notty.DEFAULT_USERS_COUNT);
		model.addAttribute("users", users);
		model.addAttribute("notices", notices);
		return Notty.APP + "/index";
	}

	@RequestMapping(value = "/notices/{id}", method = GET)
	public String notice(Model model, @PathVariable("id") long id) {
		model.addAttribute("notice", noticeService.view(id));
		return Notty.APP + "/notice";
	}

	@RequestMapping(value = "/notices/{id}/modify", method = GET)
	public String notice(Model model, HttpServletRequest request,
						 @PathVariable("id") long id) {
		AuthorizedUser user = checkLogin(request.getSession(false));
		Notice notice = noticeService.find(id);
		L.info("auth_user: {} requiring modify notice: {}",
				user.getAuthorizedName(), notice.getId(), user.getAuthorizedName());
		model.addAttribute("notice", notice);
		return Notty.APP + "/notice_modifying";
	}

	@RequestMapping(value = "/notices/create", method = POST)
	@ResponseBody
	public Notice create(Notice notice, HttpServletRequest request) {
		AuthorizedUser user = checkLogin(request.getSession(false));
		notice.setAuthor(user);
		noticeService.create(notice);
		return notice;
	}

//	bug fix of jquery file upload.
	@RequestMapping(value = "/notices/{id}/upload_file", method = GET)
	public String bug(Model model, HttpServletRequest request, @PathVariable("id") long id) {
		checkLogin(request.getSession(false));
		Notice notice = noticeService.find(id);
		model.addAttribute("notice", notice);
		return Notty.APP + "/file_upload";
	}

	@RequestMapping(value = "/notices/{id}/upload_file", method = POST)
	@ResponseBody
	public String upload(HttpServletRequest request,
						@PathVariable("id") long id,
						@RequestPart("file") Part file) {
		AuthorizedUser user = checkLogin(request.getSession(false));
		Notice notice = noticeService.find(id);
//		delete the orignal file if necessary.
		if (notice.getDocUrl() != null) {
			fileDelete(notice);
		}
		String fileName = resolvedFileName(file.getHeader("content-disposition"));
		String uri = fileUpload(file, fileName);
		notice.setDocUrl(uri);
		notice.setDocName(fileName);
		noticeService.update(notice, user.getId());
		return JSON_STATUS_OK;
	}

	@RequestMapping(value = "/notices/{id}/modify", method = PUT)
	@ResponseBody
	public Notice modify(
			HttpServletRequest request,
			@RequestParam("title") String title,
			@RequestParam("content") String content,
			@PathVariable("id") long id) {
		AuthorizedUser user = checkLogin(request.getSession(false));
		Notice notice = noticeService.find(id);
		notice.setTitle(title);
		notice.setContent(content);
		noticeService.update(notice, user.getId());
		return notice;
	}

	@RequestMapping(value = "/notices/{id}/block", method = PUT)
	@ResponseBody
	public String block(
			Model model,
			HttpServletRequest request,
			@PathVariable("id") long id) {
		AuthorizedUser user = checkLogin(request.getSession(false));
		noticeService.toggleBlock(id, user.getId());
		return JSON_STATUS_OK;
	}

	@RequestMapping(method = GET, value = "/notices/newer_than", produces = MEDIA_TYPE_JSON)
	@ResponseBody
	public String hasNew(
			Model model,
			@RequestParam("local_nid") long localNid) {
		long count = noticeService.newerCount(localNid);
		return String.format("{\"count\":%d}", count);
	}

	@RequestMapping(value = "/notices", method = GET, produces = MEDIA_TYPE_JSON)
	@ResponseBody
	public List<Notice> notices(Model model,
						  @RequestParam("count") int count,
						  @RequestParam("type") int type,
						  @RequestParam(value = "uid", defaultValue = "0") long uid,
						  @RequestParam(value = "nid", defaultValue = "0") long nid,
						  @RequestParam(value = "append", defaultValue = "false") boolean append) {
		List<Notice> notices;
		switch (type) {
			case 1:
				notices = noticeService.latest(uid, count);
				break;
			case 2:
				notices = noticeService.notices(nid, append, count);
				break;
			case 3:
				notices = noticeService.notices(uid, nid, append, count);
				break;
			default:
				notices = noticeService.latest(count);
				break;
		}
		return notices;
	}

	@RequestMapping(value = "/notices/my", method = GET)
	public String myNotices(Model model, HttpServletRequest request) {
		AuthorizedUser user = checkLogin(request.getSession(false));
		List<Notice> notices = noticeService.latest(user.getId(), Notty.MAX_NOTICES_COUNT);
//		List<Notice> notices = noticeService.latest(user.getId(), 2);
		model.addAttribute("notices", notices);
		return Notty.APP + "/notices";
	}

	@RequestMapping(value = "/notices/q", method = GET, produces = MEDIA_TYPE_JSON)
	@ResponseBody
	public List<Notice> search(Model model, @RequestParam("q") String q, @RequestParam("count") int count) {
		return noticeService.search(q, count);
	}

	@RequestMapping(value = "/notices/sync", method = GET, produces = MEDIA_TYPE_JSON)
	@ResponseBody
	public List<Notice> sync(@RequestParam("last_timestamp") long last, @RequestParam("count") int count) {
		return noticeService.sync(last, count);
	}

	private static String resolvedFileName(String header) {
		int a = header.indexOf("filename");
		return header.substring(a + 10, header.length() - 1);
	}

	private boolean uploadable(Part file) {
//		check file size first...
		if (file.getSize() > Notty.MAX_FILE_SIZE) {
			throw new IllegalArgumentException(Notty.FILE_SIZE_OVERFLOW);
		}
//		check file type, just check the filename, not content type
		String header = file.getHeader("content-disposition");
		L.info("header:{}", header);
//		filename="windows8.1.zip"
		String ext = TextUtils.getFileExt(resolvedFileName(header));
		JsonArray types = Notty.ACCEPT_FILE_TYPE;
		for (int i = 0; i < types.size(); i++) {
			if (ext.equalsIgnoreCase(types.getString(i))) {
				return true;
			}
		}
		throw new IllegalArgumentException(Notty.FILE_TYPE_NOT_ALLOW);
	}

	private String fileUpload(Part file, String fileName) {
		if (file.getSize() < 0) {
			try {
				file.delete();
			} catch (IOException e) {
				// do nothing
			}
			return null;
		}
		uploadable(file);
		String savedFileName = System.currentTimeMillis()
			+ TextUtils.getFileExt(resolvedFileName(file.getHeader("content-disposition")));
		try {
			file.write(savedFileName);
		} catch (IOException e) {
			throw new RuntimeException(Notty.FILE_UPLOAD_FAIL, e);
		}
		return Notty.UPLOAD_RELATIVE_DIR + savedFileName;
	}

	private void fileDelete(Notice notice) throws RuntimeException {
			File f = new File(Notty.UPLOAD_ABSOLUTE_DIR + notice.getDocUrl());
			if (!f.delete()) {
				throw new RuntimeException(Notty.DELETE_FILE_ERROR);
			}
			notice.setDocName(null);
			notice.setDocUrl(null);
	}

	/**
	 * 由于使用了REST API，原有的拦截器已经不适用了，故暂时使用这一方法，为接下来的spring security做准备
	 */
	private AuthorizedUser checkLogin(HttpSession session) {
		Object attribute = session.getAttribute(Notty.SESSION_USER);
		Assert.notNull(Notty.REQUIRED_LOGIN, attribute);
		return (AuthorizedUser) attribute;
	}

}
