/*
 * Copyright 2001-2013 newgxu.cn the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
public class AjaxConstants {
	
	/** json格式，包含utf8编码 */
	public static final String MEDIA_TYPE_JSON = "application/json;charset=utf-8";
	
	/** ajax请求的状态 */
	public static final String AJAX_STATUS = "status";
	
	/** ajax请求返回的信息键 */
	public static final String AJAX_MESSAGE = "msg";
	
	/** 产生异常的原因 */
	public static final String EXP_REASON = "reason";
	
	/** 默认的ajax请求成功标记 */
	public static final String JSON_STATUS_OK = "{\"status\":\"ok\"}";
	
	/** 默认的ajax请求失败标记 */
	public static final String JSON_STATUS_NO = "{\"status\":\"no\"}";
	
	/** 默认的登录拦截成功信息 */
	public static final String JSON_STATUS_NON_LOGIN = "{\"status\":\"no\",\"msg\":\"non_login\"}";
	
}
