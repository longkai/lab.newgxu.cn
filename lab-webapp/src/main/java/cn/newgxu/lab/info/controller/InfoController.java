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

import cn.newgxu.lab.core.common.AjaxConstants;
import cn.newgxu.lab.info.config.Config;
import cn.newgxu.lab.info.entity.AuthorizedUser;
import cn.newgxu.lab.info.entity.Information;
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

	private static final Logger L = LoggerFactory.getLogger(InfoController.class);
	
	@Inject
	private InfoService infoService;
	
	@RequestMapping({"/", "index", "home"})
	public String index(Model model) {
		model.addAttribute("info_list", infoService.list(0, Config.DEFAULT_INFO_LIST_COUNT));
		return Config.APP + "/index";
	}
	
	@RequestMapping("/info")
	public String view(@RequestParam("id") long id, Model model) {
		Information info = infoService.find(id);
		model.addAttribute("info", info);
		return Config.APP + "/view";
	}
	
//	REST API!
	@RequestMapping(value = "/info/{id}", produces = AjaxConstants.MEDIA_TYPE_JSON)
	@ResponseBody
	public String view(@PathVariable("id") long id) {
		Information info = infoService.find(id);
		return new JSONObject(info).toString();
	}
	
	@RequestMapping(value = "/info/create", method = RequestMethod.POST, produces = AjaxConstants.MEDIA_TYPE_JSON)
	@ResponseBody
	public String create(Information info, HttpSession session) throws JSONException {
		AuthorizedUser au = (AuthorizedUser) session.getAttribute(Config.SESSION_USER);
		info.setUser(au);
		infoService.create(info);
		
		JSONObject json = new JSONObject(info);
		json.put(AjaxConstants.AJAX_STATUS, "ok");
		return json.toString();
	}
	
	@RequestMapping(value = "/info/modify", method = RequestMethod.GET)
	public String modify(@RequestParam("id") long id, Model model) {
		L.info("请求修改发布信息id：{}", id);
		Information info = infoService.find(id);
		model.addAttribute("info", info);
		return Config.APP + "/modify";
	}
	
	@RequestMapping(value = "/info/modify", method = RequestMethod.POST, produces = AjaxConstants.MEDIA_TYPE_JSON)
	@ResponseBody
	public String modify(Information info, HttpSession session) throws JSONException {
		AuthorizedUser au = (AuthorizedUser) session.getAttribute(Config.SESSION_USER);
		L.info("用户：{} 尝试更新信息:{}", au.getAuthorizedName(), info.getTitle());
		info.setUser(au);
		infoService.update(info);
		
		JSONObject json = new JSONObject(info);
		json.put(AjaxConstants.AJAX_STATUS, "ok");
		return AjaxConstants.JSON_STATUS_OK;
	}
	
	@RequestMapping(value = "/info/newer_than/{local_id}", produces = AjaxConstants.MEDIA_TYPE_JSON)
	@ResponseBody
	public String hasNew(@PathVariable("local_id") long id) throws JSONException {
		int count = infoService.newerCount(id);
		if (count != 0) {
			JSONObject json = new JSONObject();
			json.put(AjaxConstants.AJAX_STATUS, "ok");
			json.put("count", count);
		}
		return AjaxConstants.JSON_STATUS_NO;
	}
	
	@RequestMapping(value = "/info/list/{offset}/{count}", produces = AjaxConstants.MEDIA_TYPE_JSON)
	@ResponseBody
	public String list(@PathVariable("offset") int offset, @PathVariable("count") int count) {
		List<Information> list = infoService.list(offset, count);
		return new JSONArray(list).toString();
	}
	
}
