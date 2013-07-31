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

}
