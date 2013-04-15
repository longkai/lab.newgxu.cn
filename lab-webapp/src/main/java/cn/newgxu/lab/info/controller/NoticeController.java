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
package cn.newgxu.lab.info.controller;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import cn.newgxu.lab.core.common.AjaxConstants;
import cn.newgxu.lab.core.util.Assert;
import cn.newgxu.lab.core.util.RegexUtils;
import cn.newgxu.lab.info.config.Config;
import cn.newgxu.lab.info.entity.AuthorizedUser;
import cn.newgxu.lab.info.entity.Notice;
import cn.newgxu.lab.info.service.AuthService;
import cn.newgxu.lab.info.service.NoticeService;

/**
 * 信息发布平台关于信息发布查看修改等的控制器。
 * 
 * @author longkai
 * @email im.longkai@gmail.com
 * @since 2013-3-29
 * @version 0.1
 */
@Controller
@RequestMapping("/" + Config.APP)
@Scope("session")
public class NoticeController {

	private static final Logger L =
			LoggerFactory.getLogger(NoticeController.class);
	
	@Inject
	private NoticeService noticeService;
	
	@Inject
	private AuthService authService;
	
	@RequestMapping(value = {"/", "index", "home"}, method = RequestMethod.GET)
	public String index(Model model) {
		List<Notice> notices = noticeService.latest();
		List<AuthorizedUser> users = authService.latest();
		model.addAttribute("users", users);
		model.addAttribute("notices", notices);
		return Config.APP + "/index";
	}
	
	@RequestMapping(value = "/notices/{notice_id}", method = RequestMethod.GET)
	public String get(
			Model model,
			HttpSession session, 
			@PathVariable("notice_id") long id,
			@RequestParam(value = "modifying", required = false)
				boolean modifying) {
		Notice notice = noticeService.view(id);
		model.addAttribute("notice", notice);
		if (modifying) {
			AuthorizedUser user = checkLogin(session);
			if (!notice.getUser().equals(user)) {
				throw new SecurityException("对不起，您无权修改这篇公告！");
			}
			return Config.APP + "/notice_modifying";
		}
		return Config.APP + "/notice";
	}
	
	@RequestMapping(
		value 		= "/notices",
		method 		= RequestMethod.POST
	)
	public String create(
			Notice notice,
			HttpSession session,
			RedirectAttributes attributes,
			@RequestParam("file") MultipartFile file,
			@RequestParam("file_name") String fileName) {
		AuthorizedUser au = checkLogin(session);
		
		fileUpload(notice, fileName, file);
		
		notice.setUser(au);
		notice.setContent(notice.getContent());
		noticeService.create(notice);
//		重定向，避免用户刷新重复提交。
		attributes.addAttribute("from", -1);
		attributes.addAttribute("status", "ok");
		return "redirect:/" + Config.APP + "/notices/" + notice.getId();
	}

	@RequestMapping(value = "/notices/{notice_id}", method = RequestMethod.POST)
	public String modify(
			Notice notice,
			HttpSession session, 
			RedirectAttributes attributes,
			@PathVariable("notice_id") long nid,
			@RequestParam("name") String fileName,
			@RequestParam("file") MultipartFile file) {
		AuthorizedUser au = checkLogin(session);
		Notice persistentNotice = noticeService.find(nid);
		Assert.notNull("对不起，您所请求的资源不存在！", persistentNotice);
		if (!persistentNotice.getUser().equals(au)) {
			throw new SecurityException("对不起，您无权修改！");
		}
		if (!file.isEmpty()) {
			fileDelete(persistentNotice);
		}
		persistentNotice.setTitle(notice.getTitle());
		persistentNotice.setContent(notice.getContent());

		fileUpload(notice, fileName, file);
		persistentNotice.setDocName(notice.getDocName());
		persistentNotice.setDocUrl(notice.getDocUrl());
		
		noticeService.update(persistentNotice);
		
		attributes.addAttribute("from", -1);
		attributes.addAttribute("status", "ok");
		return "redirect:/" + Config.APP + "/notices/" + nid;
	}
	
	@RequestMapping(
		value = "/notices/{notice_id}",
		method = RequestMethod.DELETE
	)
	public String block(
			Model model,
			HttpSession session,
			@PathVariable("notice_id") long nid,
			@RequestParam("blocked") boolean blocked) {
		AuthorizedUser au = checkLogin(session);
		
		Notice notice = noticeService.find(nid);
		notice.setUser(au);
		if (blocked) {
			noticeService.block(notice, true);
		} else {
			noticeService.block(notice, false);
		}
		model.addAttribute(AjaxConstants.AJAX_STATUS, "ok");
		return AjaxConstants.BAD_REQUEST;
	}
	
	@RequestMapping(
		params	 = {"has_new"},
		method	 = RequestMethod.GET,
		value	 = "/notices/{last_id}"
	)
	public String hasNew(Model model, @PathVariable("last_id") long nid) {
		int count = noticeService.newerCount(nid);
		model.addAttribute("count", count);
		return AjaxConstants.BAD_REQUEST;
	}
	
	@RequestMapping(
		params	 = {"more"},
		value	 = "/notices",
		method	 = RequestMethod.GET
	)
	public String list(
			Model model,
			@RequestParam("count") int count,
			@RequestParam("last_id") long lastId) {
		List<Notice> list = noticeService.more(lastId, count);
		model.addAttribute("notices", list);
		return AjaxConstants.BAD_REQUEST;
	}
	
	@RequestMapping(
		params	 = {"uid"},
		value	 = "/notices",
		method	 = RequestMethod.GET
	)
	public String list(
			Model model,
			HttpSession session,
			@RequestParam("uid") long uid,
			@RequestParam(value = "count", required = false) Integer count,
			@RequestParam(value = "last_notice_id", required = false)
				Long lastNid) {
		AuthorizedUser au = checkLogin(session);
		List<Notice> list = null;
		if (count == null) {
			list = noticeService.listByUser(au, Config.DEFAULT_NOTICES_COUNT);
			model.addAttribute("notices", list);
			return Config.APP + "/notices";
		}
		list = noticeService.moreByUser(au, lastNid, count);
		model.addAttribute("notices", list);
		return AjaxConstants.BAD_REQUEST;
	}
	
	private boolean uploadable(MultipartFile file) {
		if (file.getSize() > Config.MAX_FILE_SIZE) {
			throw new IllegalArgumentException("上传文件大于5M！上传失败！");
		}
		
		String fileName = file.getOriginalFilename();
		if (RegexUtils.uploadable(fileName)) {
			return true;
		}
		throw new IllegalArgumentException("上传文件类型不符合规则，上传失败！");
	}
	
	private void fileUpload(Notice info, String fileName,
			MultipartFile file) {
		try {
			if (!file.isEmpty()) {
				uploadable(file);
				String originName = file.getOriginalFilename();
				Calendar now = Calendar.getInstance();
				String path = now.get(Calendar.YEAR)
						+ "/" + (now.get(Calendar.MONTH) + 1);
				File dir = new File(Config.UPLOAD_ABSOLUTE_DIR 
						+ Config.UPLOAD_RELATIVE_DIR + path + "/");
				if (!dir.exists()) {
					if (!dir.mkdirs()) {
						throw new RuntimeException("写入文件时出错！请联系管理员！");
					}
				}
				
				String savedFileName = now.getTimeInMillis()
						+ RegexUtils.getFileExt(originName);
				file.transferTo(new File(dir.getAbsolutePath()
						+ "/" + savedFileName));
				info.setDocUrl(Config.UPLOAD_RELATIVE_DIR
						+ path + "/" + savedFileName);
				info.setDocName(fileName);
			}
		} catch (IllegalStateException e) {
			L.error("文件上传失败！", e);
			throw new RuntimeException("文件上传失败！", e);
		} catch (IOException e) {
			L.error("文件上传失败！", e);
			throw new RuntimeException("文件上传失败！", e);
		}
	}

	private void fileDelete(Notice notice) throws RuntimeException {
		if (notice.getDocUrl() != null) {
			File f = new File(Config.UPLOAD_ABSOLUTE_DIR + notice.getDocUrl());
			if (!f.delete()) {
				throw new RuntimeException("删除原有的文件失败！请稍后再试或者联系管理员！");
			}
		}
	}
	
	/** 由于使用了REST API，原有的拦截器已经不适用了，故暂时使用这一方法，为接下来的spring security做准备 */
	private AuthorizedUser checkLogin(HttpSession session) {
		Object attribute = session.getAttribute(Config.SESSION_USER);
		Assert.notNull("对不起，请您登陆后再操作！", attribute);
		return (AuthorizedUser) attribute;
	}
	
}
