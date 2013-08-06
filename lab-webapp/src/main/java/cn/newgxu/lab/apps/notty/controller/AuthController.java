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

import cn.newgxu.lab.apps.notty.Notty;
import cn.newgxu.lab.apps.notty.entity.AuthorizedUser;
import cn.newgxu.lab.apps.notty.service.AuthService;
import cn.newgxu.lab.core.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.List;

import static cn.newgxu.lab.apps.notty.Notty.*;
import static cn.newgxu.lab.core.common.ViewConstants.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * 信息发布平台认证用户的主控制器。
 *
 * @author longkai
 * @email im.longkai@gmail.com
 * @since 2013-8-3
 * @version 0.1
 */
@Controller
@Scope("session")
public class AuthController {

	private static Logger L = LoggerFactory.getLogger(AuthController.class);

	@Inject
	private AuthService	authService;

	/**
	 * RESTful API，用POST请求创建一个由服务生成器URI标识的用户。
	 * @param user
	 * @param password 确认密码
	 * @return json，假设不是json，那么就会返回一个错误的html页面。
	 */
	@RequestMapping(value = "/auth_users/create", method = POST, produces = MEDIA_TYPE_JSON)
	@ResponseBody
	public AuthorizedUser create(HttpServletRequest request,
			@ModelAttribute("user") AuthorizedUser user,
			@RequestParam("confirmed_password") String password) {
		L.info("trying registeration: org: {}, name: {}", user.getOrg(), user.getAuthorizedName());
		checkAdmin(request.getSession(false));
		authService.create(user, password);
		return user;
	}

	@RequestMapping(value = "/auth_users/auth", method = GET)
	public String auth(HttpServletRequest request) {
		checkAdmin(request.getSession(false));
		return Notty.APP + "/auth";
	}

//	管理员给用户重置密码
	@RequestMapping(value = "/auth_users/reset_password", method = PUT, produces = MEDIA_TYPE_JSON)
	@ResponseBody
	public String resetPassword(HttpServletRequest request,
								@RequestParam("uid") long uid,
								@RequestParam("password") String password) {
		checkAdmin(request.getSession(false));
		AuthorizedUser user = authService.find(uid);
		authService.resetPassword(user, password, password, null);
		return JSON_STATUS_OK;
	}

	/**
	 * RESTful API，用GET请求返回一个唯一的URI标识
	 * @param request
	 * @param password
	 * @param account
	 * @return only json
	 */
	@RequestMapping(value = "/notice/login", method = GET, produces = MEDIA_TYPE_JSON)
	@ResponseBody
	public AuthorizedUser login(HttpServletRequest request,
			@RequestParam("pwd") String password,
			@RequestParam("account") String account) {
		String ip = request.getRemoteAddr();
		AuthorizedUser user = authService.login(account, password, ip);
		request.getSession().setAttribute(Notty.SESSION_USER, user);
		return user;
	}

	/**
	 * RESTful API，使用PUT方式来退出。
	 * @param request
	 * @return only josn
	 */
	@RequestMapping(value = "/notice/logout", method = GET, produces = MEDIA_TYPE_JSON)
	@ResponseBody
	public String logout(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		return JSON_STATUS_OK;
	}

	@RequestMapping(value = "/auth_users/{uid}/password", method = PUT, produces = MEDIA_TYPE_JSON)
	@ResponseBody
	public String modifyPassword(HttpServletRequest request,
			@PathVariable("uid") long uid,
			@RequestParam("old_password") String oldPassword,
			@RequestParam("new_password") String newPasswrod,
			@RequestParam("confirmed_password") String password) {
		AuthorizedUser user = checkLogin(request.getSession(false));
		checkPermission(user, uid);
		authService.resetPassword(user, newPasswrod, password, oldPassword);
		return JSON_STATUS_OK;
	}

	@RequestMapping(value = "/auth_users/{uid}/profile", method = PUT)
	@ResponseBody
	public String modifyProfile(HttpServletRequest request,
				@PathVariable("uid") long uid,
				@RequestParam("about") String about,
				@RequestParam("contact") String contact,
				@RequestParam("password") String password) {
		AuthorizedUser user = checkLogin(request.getSession(false));
		checkPermission(user, uid);

		user.setAbout(about);
		user.setContact(contact);
		authService.update(user, password);
		return JSON_STATUS_OK;
	}

	@RequestMapping(value = "/auth_users/{uid}/modify", method = GET)
	public String modify(Model model, HttpServletRequest request,
			@PathVariable("uid") long uid) {
		AuthorizedUser user = checkLogin(request.getSession(false));
		checkPermission(user, uid);
		model.addAttribute("user", user);
		return Notty.APP + "/user_modifying";
	}

	/**
	 * 使用REST API的方式查看用户的信息。此外，也包括了认证用户请求修改个人信息的请求。
	 * @param model
	 * @param uid
	 * @return Json view
	 */
	@RequestMapping(value = "/auth_users/{uid}", method = GET, produces = MEDIA_TYPE_JSON)
	@ResponseBody
	public AuthorizedUser profile(Model model, @PathVariable("uid") long uid) {
		return authService.find(uid);
	}

	@RequestMapping(value = "/auth_users/view", method = GET)
	public String viewUsers(Model model, HttpServletRequest request) {
		checkAdmin(request.getSession(false));
		// 20 is the initial number, there is a more butoon here 0.0
		List<AuthorizedUser> users = authService.users(AuthService.ALL, 20);
		model.addAttribute("users", users);
		return Notty.APP + "/users";
	}

	@RequestMapping(value = "/auth_users", method = GET, produces = MEDIA_TYPE_JSON)
	@ResponseBody
	public List<AuthorizedUser> users(Model model,
		@RequestParam("type") int type,
		@RequestParam("count") int count,
		@RequestParam(value = "offset", defaultValue = "0") long offset,
		@RequestParam(value = "append", defaultValue = "false") boolean append) {
	    List<AuthorizedUser> users =
	    	 offset <= 0 ? authService.users(type, count)
	    	 			 : authService.users(type, count, append, offset);
		return users;
	}

	/** TODO: 由于使用了REST API，原有的拦截器已经不适用了，故暂时使用这一方法，为接下来的spring security做准备 */
	private AuthorizedUser checkLogin(HttpSession session) {
		Assert.notNull(Notty.REQUIRED_LOGIN, session);
		AuthorizedUser user =
				(AuthorizedUser) session.getAttribute(Notty.SESSION_USER);
		Assert.notNull(Notty.REQUIRED_LOGIN, user);
		return user;
	}

	/** 同上，这回是管理员的登陆拦截 */
	private AuthorizedUser checkAdmin(HttpSession session) {
		AuthorizedUser user = checkLogin(session);
		if (!user.getType().equals(AuthorizedUser.AccountType.ADMIN)) {
			throw new SecurityException(Notty.NO_PERMISSION);
		}
		return user;
	}

	private void checkPermission(AuthorizedUser user, long uid) {
		if (uid != user.getId()) {
			throw new SecurityException(Notty.NO_PERMISSION);
		}
	}

}
