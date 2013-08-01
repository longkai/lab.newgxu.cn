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
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
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

import cn.newgxu.lab.core.common.ViewConstants;
import cn.newgxu.lab.core.util.Assert;
import cn.newgxu.lab.core.util.RegexUtils;
import cn.newgxu.lab.apps.notty.config.Config;
import cn.newgxu.lab.apps.notty.entity.AuthorizedUser;
import cn.newgxu.lab.apps.notty.entity.Notice;
import cn.newgxu.lab.apps.notty.service.AuthService;
import cn.newgxu.lab.apps.notty.service.NoticeService;

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
	
	/** 最新信息 */
	private static final int	LATEST						= 1;
	/** 更多信息 */
	private static final int	MORE_NOTICES				= 2;
	/** 某个授权用户的更多信息 */
	private static final int	MORE_NOTICES_FROM_A_USER	= 3;
	/** 抓取最新的信息更新 */
	private static final int	FETCH_UPDATE_NOTICES		= 4;
	
	@Inject
	private NoticeService noticeService;
	
	@Inject
	private AuthService authService;
	
	@RequestMapping(value = {"/", "index", "home", ""}, method = RequestMethod.GET)
	public String index(Model model) {
		List<Notice> notices = noticeService.latest(Config.DEFAULT_NOTICES_COUNT);
		List<AuthorizedUser> users = authService.latest(Config.DEFAULT_USERS_COUNT);
		model.addAttribute("users", users);
		model.addAttribute("notices", notices);
		return Config.APP + "/index";
	}
	
	@RequestMapping(value = "/notices/{notice_id}", method = RequestMethod.GET)
	public String get(
			Model model,
			HttpSession session, 
			@PathVariable("notice_id") long id,
			@RequestParam(value = "modify", required = false, defaultValue = "false")
				boolean modify) {
		Notice notice = noticeService.view(id);
		model.addAttribute("notice", notice);
		if (modify) {
			AuthorizedUser user = checkLogin(session);
			if (!notice.getAuthor().equals(user)) {
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
			@RequestParam(value = "file", required = false) MultipartFile file,
			@RequestParam(value = "file_name", required = false) String fileName) {
		AuthorizedUser au = checkLogin(session);
		
		fileUpload(notice, fileName, file);
		
		notice.setAuthor(au);
		notice.setContent(notice.getContent());
		noticeService.create(notice);
//		重定向，避免用户刷新重复提交。
		attributes.addAttribute("from", -1);
		attributes.addAttribute("status", ViewConstants.OK);
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
		if (!persistentNotice.getAuthor().equals(au)) {
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
		attributes.addAttribute("status", ViewConstants.OK);
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
		notice.setAuthor(au);
		if (blocked) {
			noticeService.block(notice, true);
		} else {
			noticeService.block(notice, false);
		}
		model.addAttribute(ViewConstants.AJAX_STATUS, ViewConstants.OK);
		return ViewConstants.BAD_REQUEST;
	}
	
	@RequestMapping(
		method	 = RequestMethod.GET,
		value	 = "/notices/newer_than"
	)
	public String hasNew(
			Model model,
			@RequestParam("local_nid") long localNid) {
		long count = noticeService.newerCount(localNid);
		model.addAttribute("count", count);
		return ViewConstants.BAD_REQUEST;
	}
	
	@RequestMapping(
		value = "/notices",
		method = RequestMethod.GET
	)
	public String notices(
			Model model,
			HttpServletRequest request,
			@RequestParam("type") int type,
			@RequestParam(value = "count", defaultValue = "20") int count,
			@RequestParam(value = "last_nid", defaultValue = "9999") long lastNid,
			@RequestParam(value = "uid", defaultValue = "0") long uid,
			@RequestParam(value = "local_nid", defaultValue = "-1") long localNid) {
		List<Notice> notices = null;
		switch (type) {
		case LATEST:
			notices = noticeService.latest(count);
			break;
		case MORE_NOTICES:
			notices = noticeService.more(lastNid, count);
			break;
		case MORE_NOTICES_FROM_A_USER:
			AuthorizedUser au = checkLogin(request.getSession(false));
			if (count == 20) {
//				没有提供count，说明用户查看自己已经的发表的信息
				notices = noticeService.listByUser(au, Config.DEFAULT_NOTICES_COUNT);
				model.addAttribute("notices", notices);
				return Config.APP + "/notices";
			}
			notices = noticeService.moreByUser(au, lastNid, count);
			break;
		case FETCH_UPDATE_NOTICES:
//			判断一下，如果客户端没有提供本地的最新的id，并且也请求更新，那么我们返回最新的
			if (localNid == -1) {
				notices = noticeService.latest(count);
			} else {
				notices = noticeService.listNewer(localNid, count);
			}
			break;
		default:
			throw new IllegalArgumentException("对不起，不存在[type = " + type + "]的选项");
		}
		model.addAttribute("notices", notices);
		return ViewConstants.BAD_REQUEST;
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
