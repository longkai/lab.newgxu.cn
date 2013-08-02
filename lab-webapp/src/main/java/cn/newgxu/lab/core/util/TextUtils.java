/*
 * The MIT License (MIT)
 * Copyright (c) 2013 longkai(龙凯)
 */

package cn.newgxu.lab.core.util;

/**
 * @author longkai
 * @since 13-7-31
 * @email im.longkai@gmail.com
 * @version 0.1.0.12-7-31
 */
public class TextUtils {

	public static final boolean isEmpty(CharSequence text) {
		return text == null || text.length() == 0 ? true : false;
	}

	public static final boolean isEmpty(CharSequence text, boolean trim) {
		return trim ? isEmpty(text.toString().trim()) : isEmpty(text);
	}

	public static final String getter(String name) {
		return getterOrsetter(name, true);
	}

	public static final String setter(String name) {
		return getterOrsetter(name, false);
	}

	public static String getterOrsetter(String str, boolean toGetter) {
		char c = str.charAt(0);
		if ('a' <= c && c <= 'z') {
			c = Character.toUpperCase(c);
			str =  c + str.substring(1);
		}
		return toGetter ? "get" + str : "set" + str;
	}

}
