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
package cn.newgxu.lab.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式的一个工具类。
 * 
 * @author longkai
 * @since 2013-2-28
 * @version 1.0
 */
public class RegexUtils {
	
	private static Pattern pattern;
	private static Matcher matcher; 
	
	/**
	 * 初始化，稍微考虑到了一点效率。
	 * @param regex
	 */
	private static void init(String regex, String text) {
		if (pattern == null || !pattern.pattern().equals(regex)) {
			pattern = Pattern.compile(regex);
		}
		matcher = pattern.matcher(text);
	}
	
	/**
	 * 是否包含此串。
	 * @param text
	 * @param regex
	 * @return true or false.
	 */
	public static boolean contains(String text, String regex) {
		init(regex, text);
		return matcher.find();
	}
	
	/**
	 * does the text matches the pattern.
	 * @param text
	 * @param regex
	 * @return true or false.
	 */
	public static boolean matches(String text, String regex) {
		init(regex, text);
		return matcher.matches();
	}
	
	/**
	 * 抓取第一个匹配的串。
	 * @param text
	 * @param regex
	 * @return 捕捉到的第一个匹配串，否则返回null。
	 */
	public static String fetchFirst(String text, String regex) {
		init(regex, text);
		if (matcher.find()) {
			return matcher.group();
		}
		return null;
	}
	
}
