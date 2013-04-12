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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import cn.newgxu.lab.core.common.AjaxConstants;
import cn.newgxu.lab.core.util.Assert;
import cn.newgxu.lab.core.util.RegexUtils;
import cn.newgxu.lab.info.config.Config;
import cn.newgxu.lab.info.entity.AuthorizedUser;
import cn.newgxu.lab.info.entity.Information;
import cn.newgxu.lab.info.service.AuthService;
import cn.newgxu.lab.info.service.InfoService;

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
public class InfoController {

	private static final Logger L =
			LoggerFactory.getLogger(InfoController.class);
	
	@Inject
	private InfoService infoService;
	
	@Inject
	private AuthService authService;
	
	@RequestMapping({"/", "index", "home"})
	public String index(Model model) {
		List<Information> list = 
				infoService.latest();
		List<AuthorizedUser> auths = authService.latest();
		if (list != null && list.size() > 0) {
			model.addAttribute("last_info_id", list.get(list.size() - 1).getId());
		}
		if (auths != null && auths.size() > 0) {
			model.addAttribute("last_user_id", auths.get(auths.size() - 1).getId());
		}
		model.addAttribute("auth_list", auths);
		model.addAttribute("info_list", list);
		return Config.APP + "/index";
	}
	
	@RequestMapping("/info")
	public String view(@RequestParam("id") long id, Model model) {
		Information info = infoService.view(id);
		if (info == null) {
			throw new IllegalArgumentException("对不起，您所查找的信息不存在！");
		}
		model.addAttribute("info", info);
		return Config.APP + "/view";
	}
	
//	REST API!
	@RequestMapping(
		value	 = "/info/{id}",
		produces = AjaxConstants.MEDIA_TYPE_JSON
	)
	@ResponseBody
	public String view(@PathVariable("id") long id) {
		Information info = infoService.find(id);
		return new JSONObject(info).toString();
	}
	
	@RequestMapping(
		value 		= "/info/create",
		method 		= RequestMethod.POST,
		produces 	= AjaxConstants.MEDIA_TYPE_JSON
	)
	public String create(
			Information info,
			@RequestParam("name") String fileName,
			@RequestParam("file") MultipartFile file,
			HttpSession session, RedirectAttributes attributes
			) throws JSONException {

		fileUpload(info, fileName, file);
		
		AuthorizedUser au = 
				(AuthorizedUser) session.getAttribute(Config.SESSION_USER);
		info.setUser(au);
		info.setContent(info.getContent());
		infoService.create(info);
//		重定向，避免用户刷新重复提交。
		attributes.addFlashAttribute("from", "-1");
		attributes.addAttribute("status", "ok");
		attributes.addAttribute("id", info.getId());
		return "redirect:/" + Config.APP + "/info";
	}

	@RequestMapping(value = "/info/modify", method = RequestMethod.GET)
	public String modify(@RequestParam("id") long id, Model model) {
		L.info("请求修改发布信息id：{}", id);
		Information info = infoService.find(id);
		if (info == null) {
			throw new IllegalArgumentException("对不起，您所修改的信息不存在！");
		}
		model.addAttribute("info", info);
		return Config.APP + "/modify_info";
	}
	
	@RequestMapping(
		value	 = "/info/modify",
		method	 = RequestMethod.POST
//		produces = AjaxConstants.MEDIA_TYPE_JSON
	)
	public String modify(Information info,
			@RequestParam("name") String fileName,
			@RequestParam("file") MultipartFile file,
			HttpSession session, RedirectAttributes attributes)
			throws JSONException {
		fileUpload(info, fileName, file);
		AuthorizedUser au = 
				(AuthorizedUser) session.getAttribute(Config.SESSION_USER);
		info.setUser(au);
		infoService.update(info);
		
		attributes.addFlashAttribute("from", "-1");
		attributes.addAttribute("status", "ok");
		attributes.addAttribute("id", info.getId());
		return "redirect:/" + Config.APP + "/info";
	}
	
	@RequestMapping(
		value	 = "/info/block/{type}/{id}",
		produces = AjaxConstants.MEDIA_TYPE_JSON
	)
	@ResponseBody
	public String block(@PathVariable("type") String type,
			@PathVariable("id") long id,
			HttpSession session) {
		Assert.notNull("操作类型不能为空！", type);
		AuthorizedUser au
			= (AuthorizedUser) session.getAttribute(Config.SESSION_USER);
		Information info = new Information();
		info.setId(id);
		info.setUser(au);
		if (type.equals("block")) {
			infoService.block(info, true);
		} else if (type.equals("unblock")) {
			infoService.block(info, false);
		} else {
			throw new UnsupportedOperationException("不支持的操作！");
		}
		return AjaxConstants.JSON_STATUS_OK;
	}
	
	@RequestMapping(
		value	 = "/info/newer_than/{local_id}",
		produces = AjaxConstants.MEDIA_TYPE_JSON
	)
	@ResponseBody
	public String hasNew(@PathVariable("local_id") long id)
			throws JSONException {
		int count = infoService.newerCount(id);
		if (count != 0) {
			JSONObject json = new JSONObject();
			json.put(AjaxConstants.AJAX_STATUS, "ok");
			json.put("count", count);
			return json.toString();
		}
		return AjaxConstants.JSON_STATUS_NO;
	}
	
	@RequestMapping(
		value	 = "/info/list/{last_id}/{count}",
		produces = AjaxConstants.MEDIA_TYPE_JSON
	)
	@ResponseBody
	public String list(
			@PathVariable("last_id") long lastId,
			@PathVariable("count") int count) {
		List<Information> list = infoService.more(lastId, count);
		return new JSONArray(list, false).toString();
	}
	
	@RequestMapping(
		value	 = "/info/list/user/{uid}/{last_id}/{count}",
		produces = AjaxConstants.MEDIA_TYPE_JSON
	)
	@ResponseBody
	public String list(
			@PathVariable("uid") long uid,
			@PathVariable("last_id") int lastId,
			@PathVariable("count") int count,
			HttpSession session) {
		AuthorizedUser au
			= (AuthorizedUser) session.getAttribute(Config.SESSION_USER);
		List<Information> list = infoService.moreByUser(au, lastId, count);
		return new JSONArray(list, false).toString();
	}
	
	@RequestMapping("/info/list/user/{uid}")
	public String list(@PathVariable("uid") long uid, Model model,
			HttpSession session) {
		AuthorizedUser au 
			= (AuthorizedUser) session.getAttribute(Config.SESSION_USER);
		List<Information> list
			= infoService.listByUser(au, Config.DEFAULT_INFO_LIST_COUNT);
		model.addAttribute("info_list", list);
		model.addAttribute("last_info_id", list.get(list.size() - 1).getId());
		return Config.APP + "/info_list";
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
	
	private void fileUpload(Information info, String fileName,
			MultipartFile file) {
		try {
			if (!file.isEmpty()) {
//				处理之前传过的文件，如果有
				fileDelete(info);
				
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

	private void fileDelete(Information info) throws RuntimeException {
		Information origin = infoService.find(info.getId());
//		如果没有，就代表是新建文档，直接返回吧- -
		if (origin == null) {
			return;
		}
		if (origin.getDocUrl() != null) {
			File f = new File(Config.UPLOAD_ABSOLUTE_DIR + origin.getDocUrl());
			if (!f.delete()) {
				throw new RuntimeException("删除原有的文件失败！请稍后再试或者联系管理员！");
			}
		}
	}
	
}
