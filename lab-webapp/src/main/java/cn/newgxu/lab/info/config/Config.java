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
package cn.newgxu.lab.info.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 信息发布平台的一些配置信息。
 * 
 * @author longkai
 * @email im.longkai@gmail.com
 * @since 2013-3-28
 * @version 0.1
 */
public class Config {
	
	private static final Logger L = LoggerFactory.getLogger(Config.class);
	
	/** 默认的一次用户列表请求抓取数量 */
	public static final int			DEFAULT_USERS_COUNT;

	/** 一次性最多抓取的用户列表数 */
	public static final int			MAX_USERS_COUNT;

	/** 一次性最多抓取的信息列表数 */
	public static final int			MAX_NOTICES_COUNT;

	/** 默认的一次信息列表请求抓取数量 */
	public static final int			DEFAULT_NOTICES_COUNT;

	/** 密码的最小长度 */
	public static final int			MIN_PASSWORD_LENGTH;

	/** 账号的最小长度 */
	public static final int			MIN_ACCOUNT_LENGTH;

	/** 文件上传最大大小 */
	public static final long		MAX_FILE_SIZE;

	private static Properties	props;
	
	static {
		props				  = new Properties();
		InputStream in = null;
		try {
			in = Config.class.getResourceAsStream("/config/info.properties");
			props.load(in);
		} catch (IOException e) {
			L.error("启动配置文件时出错！", e);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				L.error("wtf!", e);
			}
		}
		MAX_FILE_SIZE 		  = Long.parseLong(props.getProperty("MAX_FILE_SIZE"));
		MAX_USERS_COUNT 	  = Integer.parseInt(props.getProperty("MAX_USERS_COUNT"));
		MAX_NOTICES_COUNT	  = Integer.parseInt(props.getProperty("MAX_NOTICES_COUNT"));
		MIN_ACCOUNT_LENGTH 	  = Integer.parseInt(props.getProperty("MIN_ACCOUNT_LENGTH"));
		DEFAULT_USERS_COUNT	  = Integer.parseInt(props.getProperty("DEFAULT_USERS_COUNT"));
		MIN_PASSWORD_LENGTH	  = Integer.parseInt(props.getProperty("MIN_PASSWORD_LENGTH"));
		DEFAULT_NOTICES_COUNT = Integer.parseInt(props.getProperty("DEFAULT_NOTICES_COUNT"));
	}
	
	/** 应用名称，必须硬编码- - */
	public static final String		APP						= "info";

	/** session属性的认证用户 */
	public static final String		SESSION_USER			= props.getProperty("SESSION_USER");


	/** 文件上传存放的相对路径 */
	public static final String		UPLOAD_RELATIVE_DIR		= props.getProperty("UPLOAD_RELATIVE_DIR");

	/** 加密密码前的附加前缀，稍微增强一点安全性 */
	public static final String		PASSWORD_PRIVATE_KEY	= props.getProperty("PASSWORD_PRIVATE_KEY");

	/** 文件上传的绝对路径 */
	public static final String		UPLOAD_ABSOLUTE_DIR		= props.getProperty("UPLOAD_ABSOLUTE_DIR");

}
