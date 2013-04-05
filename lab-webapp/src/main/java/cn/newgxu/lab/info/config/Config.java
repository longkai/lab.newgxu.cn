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
	
	/** 默认的一次用户列表请求抓取数量 */
	public static final int DEFAULT_USER_LIST_COUNT = 5;
	
	/** 一次性最多抓取的用户列表数 */
	public static final int MAX_USER_LIST_COUNT = 50;
	
	/** 一次性最多抓取的信息列表数 */
	public static final int MAX_INFO_LIST_COUNT = 20;
	
	/** 默认的一次信息列表请求抓取数量 */
	public static final int DEFAULT_INFO_LIST_COUNT = 3;
	
	/** 密码的最小长度 */
	public static final int MIN_PASSWORD_LENGTH = 6;
	
	/** 账号的最小长度 */
	public static final int MIN_ACCOUNT_LENGTH = 6;
	
	/** 文件上传最大大小 5M */
	public static final long MAX_FILE_SIZE = 1024 * 1000 * 5;
	
	/** 默认的上传文件保存模式 */
	public static final String DEFAULT_FILE_NAME_PATTERN = "yyyyMMddHHmmss";
	
	/** 文件上传的绝对路径 */
	public static final String UPLOAD_ABSOLUTE_DIR = "E:/usr/local/src/coding4fun/lab.newgxu.cn/lab-webapp/src/main/webapp";
	
	/** 文件上传存放的相对路径 */
	public static final String UPLOAD_RELATIVE_DIR = "/resources/upload/info/";
	
}
