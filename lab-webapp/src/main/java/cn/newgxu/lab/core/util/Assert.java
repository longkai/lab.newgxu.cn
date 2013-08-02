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

/**
 * 断言实用工具类。
 * 
 * @author longkai
 * @email im.longkai@gmail.com
 * @since 2013-3-29
 * @version 0.1
 */
public class Assert {

	/**
	 * 断言字符串不为空并且不为空串，会主动截取两边的空白符。
	 * @param message
	 * @param str
	 */
	public static void notEmpty(String message, String str) {
		notNull(message, str);
		
		str = str.trim();
		if (str.equals("")) {
			throw new IllegalArgumentException(message);
		}
	}
	
	/**
	 * 断言字符串是否大于等于指定的长度，同样的，会主动截取两边的空白符。
	 * @param message
	 * @param str
	 * @param length
	 */
	public static void hasLength(String message, String str, int length) {
		Assert.notNull(message, str);
		
		str = str.trim();
		if (str.length() < length) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * 断言字符串是否在指定的区间，同样的，会主动截取两边的空白符。
	 * @param message
	 * @param str
	 * @param min
	 * @param max
	 */
	public static void between(String message, String str, int min, int max) {
		notNull(message, str);
		str = str.trim();
		if (str.length() < min || str.length() > max) {
			throw new IllegalArgumentException(message);
		}
	}
	
	/**
	 * 断言对象不为空。
	 * @param message
	 * @param object
	 */
	public static void notNull(String message, Object object) {
		if (object == null) {
			throw new IllegalArgumentException(message);
		}
	}
	
}
