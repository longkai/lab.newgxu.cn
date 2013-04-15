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
package cn.newgxu.lab.core.common;

/**
 * 一些常量，一般都是lab中约定的。
 * 
 * @author longkai
 * @email im.longkai@gmail.com
 * @since 2013-3-29
 * @version 0.1
 */
public class ViewConstants {
	
	/** json格式，包含utf8编码 */
	public static final String MEDIA_TYPE_JSON = "application/json;charset=utf-8";
	
	/** ajax请求的状态 */
	public static final String AJAX_STATUS = "status";
	
	/** ajax请求返回的信息键 */
	public static final String AJAX_MESSAGE = "msg";
	
	/** 产生异常的原因 */
	public static final String EXP_REASON = "reason";
	
	/** 未知的原因 */
	public static final String UNKNOWN_REASON = "我们没有能收集足够的错误信息，请您稍后再试！";
	
	/** 默认的ajax请求成功标记 */
	public static final String JSON_STATUS_OK = "{\"status\":\"ok\"}";
	
	/** 默认的ajax请求失败标记 */
	public static final String JSON_STATUS_NO = "{\"status\":\"no\"}";
	
	/** 默认的登录拦截成功信息 */
	public static final String JSON_STATUS_NON_LOGIN = "{\"status\":\"no\",\"msg\":\"non_login\",\"reason\":\"请您登陆后再进行相关操作！\"}";
	
	/** 用于只返回ajax视图而不返回默认的html视图 */
	public static final String BAD_REQUEST = "bad_request";
	
	/** 默认的错误提示试图名 */
	public static final String ERROR_PAGE = "error";
	
}
