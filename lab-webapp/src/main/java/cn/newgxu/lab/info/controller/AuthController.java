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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.newgxu.lab.core.common.ViewConstants;
import cn.newgxu.lab.core.util.Assert;
import cn.newgxu.lab.info.config.Config;
import cn.newgxu.lab.info.entity.AuthorizedUser;
import cn.newgxu.lab.info.service.AuthService;

/**
 * 信息发布平台认证用户的主控制器。
 * 
 * @author longkai
 * @email im.longkai@gmail.com
 * @since 2013-3-29
 * @version 0.1
 */
@Controller
@RequestMapping("/" + Config.APP)
@Scope("session")
public class AuthController {

	private static final Logger	L =
			LoggerFactory.getLogger(AuthController.class);

	@Inject
	private AuthService			authService;

	/**
	 * RESTful API，用POST请求创建一个由服务生成器URI标识的用户。
	 * @param au
	 * @param _pwd 确认密码
	 * @return json，假设不是json，那么就会返回一个错误的html页面。
	 */
	@RequestMapping(value = "/users", method = RequestMethod.POST)
	public String auth(
			Model model,
			@ModelAttribute("user") AuthorizedUser au,
			@RequestParam(value = "_pwd", defaultValue = "") String _pwd) {
		L.info("尝试认证用户！单位（组织）：{}，名称：{}", au.getOrg(), au.getAuthorizedName());
		authService.create(au, _pwd);
		model.addAttribute(ViewConstants.AJAX_STATUS, "ok");
//		我们只返回json数据，没有html视图哈
		return ViewConstants.BAD_REQUEST;
	}

	/**
	 * RESTful API，用GET请求返回一个唯一的URI标识
	 * @param model
	 * @param request
	 * @param password
	 * @param account
	 * @return only json, or bad request!
	 */
	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public String login(
			Model model,
			HttpServletRequest request,
			@RequestParam("pwd") String password,
			@RequestParam("account") String account) {
		String ip = request.getRemoteAddr();
		AuthorizedUser au = authService.login(account, password, ip);
		request.getSession().setAttribute(Config.SESSION_USER, au);
//		这里，登录异常交给全局异常处理！
		model.addAttribute(ViewConstants.AJAX_STATUS, "ok");
		return ViewConstants.BAD_REQUEST;
	}

	/**
	 * RESTful API，使用PUT方式来退出。
	 * @param model
	 * @param request
	 * @param uid
	 * @return only josn
	 */
	@RequestMapping(value = "/users/{uid}", method = RequestMethod.PUT)
	public String logout(
			Model model,
			HttpServletRequest request,
			@PathVariable("uid") long uid) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		model.addAttribute(ViewConstants.AJAX_STATUS, "ok");
		return ViewConstants.BAD_REQUEST;
	}

	/**
	 * REST API，使用PUT方式更新用户数据
	 * @param session
	 * @param uid
	 * @param type 修改的类型
	 * @param password
	 * @param pwd1
	 * @param pwd2
	 * @param about
	 * @param contact
	 * @return only json
	 */
	@RequestMapping(
		value	 = "/users/{uid}",
		method	 = RequestMethod.PUT,
		params	 = {"modifying_type"}
	)
	@ResponseBody
	public String modify(
			HttpSession session,
			@PathVariable("uid") long uid,
			@RequestParam("password") String password,
			@RequestParam("modifying_type") String type,
			@RequestParam(value = "pwd1", required = false) String pwd1,
			@RequestParam(value = "pwd2", required = false) String pwd2,
			@RequestParam(value = "about", required = false) String about,
			@RequestParam(value = "contact", required = false) String contact) {
		AuthorizedUser sau = checkLogin(session);
//		首页验证一下是否为同一人
		if (sau.getId() != uid) {
			throw new SecurityException("对不起，您无权修改该用户的信息！");
		}
//		然后验证一下密码是否正确。
		authService.login(sau.getAccount(), password, null);
		
		if (type.equals("password")) {
			sau.setPassword(pwd1);
			authService.resetPassword(sau, pwd2);
		} else if (type.equals("profile")) {
			sau.setContact(contact);
			sau.setAbout(about);
			authService.update(sau);
		} else {
			throw new UnsupportedOperationException("不支持的操作！");
		}
		return ViewConstants.JSON_STATUS_OK;
	}
	
	/**
	 * 使用REST API的方式查看用户的信息。此外，也包括了认证用户请求修改个人信息的请求。
	 * @param model
	 * @param session
	 * @param uid
	 * @param modifying 是否有修改个人信息的意图，默认为false
	 * @return
	 */
	@RequestMapping(value = "/users/{uid}", method = RequestMethod.GET)
	public String profile(
			Model model,
			HttpSession session,
			@PathVariable("uid") long uid,
			@RequestParam(value = "modifying", required = false)
				boolean modifying) {
		AuthorizedUser au = null;
//		如果请求修改信息，那我们返回html视图
		if (modifying) {
			au = checkLogin(session);
			Assert.notNull("对不起，请登陆后再操作！", au);
			if (au.getId() != uid) {
				throw new SecurityException("对不起，您无权修改该用户的信息！");
			}
			model.addAttribute("user", au);
			return Config.APP + "/user_modifying";
		}
//		简单的查看用户信息
		au = authService.find(uid);
		Assert.notNull("对不起，您所查看的用户不存在！", au);
		model.addAttribute(ViewConstants.AJAX_STATUS, "ok");
		model.addAttribute("user", au);
		return ViewConstants.BAD_REQUEST;
	}

	@RequestMapping(
		value	= "/users",
		method	= RequestMethod.GET,
		params	= {"last_uid"}
	)
	public String more(
			Model model, 
			@RequestParam("count") int count,
			@RequestParam("last_uid") long lastUid) {
		List<AuthorizedUser> list = authService.more(lastUid, count);
		model.addAttribute("users", list);
		return ViewConstants.BAD_REQUEST;
	}
	
	/** 由于使用了REST API，原有的拦截器已经不适用了，故暂时使用这一方法，为接下来的spring security做准备 */
	private AuthorizedUser checkLogin(HttpSession session) {
		AuthorizedUser user = (AuthorizedUser) session.getAttribute(Config.SESSION_USER);
		Assert.notNull("对不起，请您登陆后再操作！", user);
		return user;
	}
	
}
