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
