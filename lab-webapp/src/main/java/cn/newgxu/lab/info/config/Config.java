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
package cn.newgxu.lab.info.config;

/**
 * 信息发布平台的一些配置信息。
 * 
 * @author longkai
 * @email im.longkai@gmail.com
 * @since 2013-3-28
 * @version 0.1
 */
public class Config {

	/** 应用名称 */
	public static final String APP = "info";
	
	/** session属性的认证用户 */
	public static final String SESSION_USER = APP + "_user";
	
	/** 多文档的urls分隔符 */
	public static final String DOC_URL_DELIMITER = "|";
	
	/** 默认的初始密码 */
	public static final String DEFAULT_PASSWORD = "yws123456";
	
	/** 默认的账户名前缀 */
	public static final String DEFAULT_ACCOUNT_PREFIX = "yws_info_";
	
	/** 默认的账户名后缀 */
	public static final String DEFAULT_ACCOUNT_SUFFIX = "yyyyMMddHHmmss";
	
	/** 默认的一次用户列表请求抓取数量 */
	public static final int DEFAULT_USER_LIST_COUNT = 10;
	
	/** 默认的一次信息列表请求抓取数量 */
	public static final int DEFAULT_INFO_LIST_COUNT = 5;
	
}
