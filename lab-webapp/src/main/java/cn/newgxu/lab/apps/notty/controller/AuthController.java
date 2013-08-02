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

import cn.newgxu.lab.apps.notty.entity.AuthorizedUser;
import cn.newgxu.lab.apps.notty.service.AuthService;
import cn.newgxu.lab.core.common.ViewConstants;
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
import java.util.List;

import static cn.newgxu.lab.apps.notty.Notty.*;

/**
 * 信息发布平台认证用户的主控制器。
 * 
 * @author longkai
 * @email im.longkai@gmail.com
 * @since 2013-3-29
 * @version 0.1
 */
@Controller
@RequestMapping("/info")
@Scope("session")
public class AuthController {

	private static Logger L = LoggerFactory.getLogger(AuthController.class);
	
	/** 更新用户信息 */
	public static final int		MODIFY_PROFILE			= 1;
	/** 更新用户密码 */
	public static final int		MODIFY_PASSWORD			= 2;

	/** 最新授权用户列表 */
	public static final int		LASTEST_AUTHED_USERS	= 1;
	/** 更多授权用户列表 */
	public static final int		MORE_AUTHED_USERS		= 2;
	/** 未授权用户列表(内部使用) */
	public static final int		BLOCKED_USERS			= 3;
	

	@Inject
	private AuthService			authService;

	/**
	 * RESTful API，用POST请求创建一个由服务生成器URI标识的用户。
	 * @param au
	 * @param _pwd 确认密码
	 * @return json，假设不是json，那么就会返回一个错误的html页面。
	 */
	@RequestMapping(value = "/users", method = RequestMethod.POST)
	public String create(
			Model model,
			HttpServletRequest request,
			@ModelAttribute("user") AuthorizedUser au,
			@RequestParam(value = "_pwd", defaultValue = "") String _pwd) {
		L.info("trying registeration: org: {}, name: {}", au.getOrg(), au.getAuthorizedName());
		checkAdmin(request.getSession(false));
		authService.create(au, _pwd);
		model.addAttribute(ViewConstants.AJAX_STATUS, ViewConstants.OK);
		model.addAttribute("user", au);
//		我们只返回json数据，没有html视图哈
		return ViewConstants.BAD_REQUEST;
	}
	
	@RequestMapping(value = "/auth", method = RequestMethod.GET)
	public String auth(HttpServletRequest request) {
		checkAdmin(request.getSession(false));
		return "info" + "/auth";
	}
	
//	管理员给用户重置密码
	@RequestMapping(value = "/reset_pwd", method = RequestMethod.PUT)
	public String resetPwd(
			Model model,
			HttpServletRequest request,
			@RequestParam("uid") long uid,
			@RequestParam("pwd") String pwd) {
		checkAdmin(request.getSession(false));
		AuthorizedUser au = authService.find(uid);
		Assert.notNull(getMessage(NOT_FOUND), au);
//		这里，因为是由于管理员负责的，所以就从简了
		au.setPassword(pwd);
		authService.resetPassword(au, pwd);
		model.addAttribute(ViewConstants.AJAX_STATUS, ViewConstants.OK);
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
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(
			Model model,
			HttpServletRequest request,
			@RequestParam("pwd") String password,
			@RequestParam("account") String account) {
		String ip = request.getRemoteAddr();
		AuthorizedUser au = authService.login(account, password, ip);
		request.getSession().setAttribute(R.getString(SESSION_USER.name()), au);
//		这里，登录异常交给全局异常处理！
		model.addAttribute(ViewConstants.AJAX_STATUS, ViewConstants.OK);
		model.addAttribute("user", au);
		return ViewConstants.BAD_REQUEST;
	}

	/**
	 * RESTful API，使用PUT方式来退出。
	 * @param model
	 * @param request
	 * @param uid
	 * @return only josn
	 */
	@RequestMapping(value = {"/users/{uid}", "/logout"}, method = RequestMethod.PUT)
	public String logout(
			Model model,
			HttpServletRequest request,
			@PathVariable("uid") long uid) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		model.addAttribute(ViewConstants.AJAX_STATUS, ViewConstants.OK);
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
		params	 = {"type"}
	)
	@ResponseBody
	public String modify(
			HttpSession session,
			@PathVariable("uid") long uid,
			@RequestParam("type") int type,
			@RequestParam(value = "password", required = false) String password,
			@RequestParam(value = "pwd1", required = false) String pwd1,
			@RequestParam(value = "pwd2", required = false) String pwd2,
			@RequestParam(value = "about", required = false) String about,
			@RequestParam(value = "contact", required = false) String contact) {
		AuthorizedUser sau = checkLogin(session);
//		首页验证一下是否为同一人
		if (sau.getId() != uid) {
			throw new SecurityException(getMessage(NO_PERMISSION));
		}
//		然后验证一下密码是否正确。
		authService.login(sau.getAccount(), password, null);
		
		switch (type) {
		case MODIFY_PASSWORD:
			sau.setPassword(pwd1);
			authService.resetPassword(sau, pwd2);
			break;
		case MODIFY_PROFILE:
			sau.setContact(contact);
			sau.setAbout(about);
			authService.update(sau);
			break;
		default:
			throw new IllegalArgumentException(getMessage(NOT_SUPPORT) + " [type=" + type + "]");
		}
		return ViewConstants.JSON_STATUS_OK;
	}
	
	/**
	 * 使用REST API的方式查看用户的信息。此外，也包括了认证用户请求修改个人信息的请求。
	 * @param model
	 * @param session
	 * @param uid
	 * @param modify 是否有修改个人信息的意图，默认为false
	 * @return
	 */
	@RequestMapping(value = "/users/{uid}", method = RequestMethod.GET)
	public String profile(
			Model model,
			HttpSession session,
			@PathVariable("uid") long uid,
			@RequestParam(
				value = "modify", required = false, defaultValue = "false")
					boolean modify) {
		AuthorizedUser au = null;
//		如果请求修改信息，那我们返回html视图
		if (modify) {
			au = checkLogin(session);
			Assert.notNull(getMessage(REQUIRED_LOGIN), au);
			if (au.getId() != uid) {
				throw new SecurityException(getMessage(NO_PERMISSION));
			}
			model.addAttribute("user", au);
			return "info" + "/user_modifying";
		}
//		简单的查看用户信息
		au = authService.find(uid);
		Assert.notNull(getMessage(NOT_FOUND), au);
		model.addAttribute(ViewConstants.AJAX_STATUS, ViewConstants.OK);
		model.addAttribute("user", au);
		return ViewConstants.BAD_REQUEST;
	}

	@RequestMapping(
		value  = "/users",
		method = RequestMethod.GET
	)
	public String users(
			Model model,
			HttpServletRequest request,
			@RequestParam("type") int type,
			@RequestParam(
				value = "count",
				required = false,
				defaultValue = "20") int count,
			@RequestParam(
				value = "last_uid",
				required = false,
				defaultValue = "9999") long lastUid) {
//		999是一个预设值，意指检索id<9999的用户，短时间内无法到达
		List<AuthorizedUser> users = null;
		switch (type) {
		case LASTEST_AUTHED_USERS:
			users = authService.latest(count);
			break;
		case MORE_AUTHED_USERS:
			users = authService.more(lastUid, count);
			break;
		case BLOCKED_USERS:
//			TODO：这里，考虑实际情况，暂时没有分页（以后如果出现很多未授权用户的话添上）
			checkAdmin(request.getSession(false));
			users = authService.authed();
			model.addAttribute("users", users);
			return "info" + "/users"; // 暂时用于授权，内部使用，提前返回
		default:
			throw new IllegalArgumentException(
					getMessage(NOT_SUPPORT) + " [type=" + type + "]");
		}
		model.addAttribute("users", users);
		return ViewConstants.BAD_REQUEST;
	}
	
	/** TODO: 由于使用了REST API，原有的拦截器已经不适用了，故暂时使用这一方法，为接下来的spring security做准备 */
	private AuthorizedUser checkLogin(HttpSession session) {
		Assert.notNull(getMessage(REQUIRED_LOGIN), session);
		AuthorizedUser user =
				(AuthorizedUser) session.getAttribute(R.getString(SESSION_USER.name()));
		Assert.notNull(getMessage(REQUIRED_LOGIN), user);
		return user;
	}
	
	/** 同上，这回是管理员的登陆拦截 */
	private AuthorizedUser checkAdmin(HttpSession session) {
		AuthorizedUser user = this.checkLogin(session);
		if (!user.getType().equals(AuthorizedUser.AccountType.ADMIN)) {
			throw new SecurityException(getMessage(NO_PERMISSION));
		}
		return user;
	}
	
}
