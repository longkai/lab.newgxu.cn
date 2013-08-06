/*
 * The MIT License (MIT)
 * Copyright (c) 2013 longkai(龙凯)
 *
 * The software shall be used for good, not evil.
 */

package cn.newgxu.lab.core.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonReader;
import javax.json.JsonValue;
import java.io.*;
import java.net.URL;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * 读写资源的具体实现，模版方法。
 * @author longkai(龙凯)
 * @email  im.longkai@gmail.com
 * @since  13-7-30
 * @version 0.2.0.13-8-1
 */
public class Resources {

	private static final Logger L = LoggerFactory.getLogger(Resources.class);

	private static final String DEFAULT_CHARSET = "UTF-8";

	public static final Pattern TCP_IP_PROTOCAL = Pattern.compile("^[\\w|:]{3,}://");

	public static final JsonValue getJsonValue(String uri) {
		return getJsonValue(uri, null);
	}

	public static final JsonValue getJsonValue(String uri, Locale locale) {
		InputStream in = null;
		JsonReader reader = null;
		JsonValue jsonValue = null;
		try {
			in = getInputStream(uri, locale);
			reader = Json.createReader(in);
			jsonValue = reader.read();
		} catch (IOException e) {
			throw new RuntimeException("get input stream error!", e);
		} finally {
			close(null, reader, in);
		}
		return jsonValue;
	}


	public static final void readJson(String uri, ResourcesCallback callback) {
		readJson(uri, null, callback);
	}

	public static final void readJson(String uri, Locale locale, ResourcesCallback callback) {
		callback.onStart();
		InputStream in = null;
		try {
			in = getInputStream(uri, locale);
			JsonValue json = Json.createReader(in).read();
			callback.onSuccess(json);
		} catch (IOException e) {
			callback.onError(e);
		} finally {
			close(callback, in);
		}
		callback.onFinish();
	}

	public static final void loadProps(String uri, ResourcesCallback callback) {
		loadProps(uri, null, callback);
	}

	public static final void loadProps(String uri, Locale locale, ResourcesCallback callback) {
		callback.onStart();
		InputStream in = null;
		try {
			in = getInputStream(uri, locale);
			Properties props = new Properties();
			props.load(in);
			callback.onSuccess(props);
		} catch (IOException e) {
			callback.onError(e);
		} finally {
			close(callback, in);
		}
		callback.onFinish();
	}

	public static final void openBufferedReader(String uri, ResourcesCallback callback) {
		openBufferedReader(uri, null, null, callback);
	}

	public static final void openBufferedReader(String uri, Locale locale, String charset, ResourcesCallback callback) {
		callback.onStart();
		charset = TextUtils.isEmpty(charset) ? DEFAULT_CHARSET : charset;
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		try {
			inputStream = getInputStream(uri, locale);
			inputStreamReader = new InputStreamReader(inputStream, charset);
			bufferedReader = new BufferedReader(inputStreamReader);
			callback.onSuccess(bufferedReader);
		} catch (Exception e) {
			callback.onError(e);
		} finally {
			close(callback, bufferedReader, inputStreamReader, inputStream);
		}
		callback.onFinish();
	}

	public static final void openBufferedWriter(String uri, boolean append, ResourcesCallback callback) {
		openBufferedWriter(uri, append, null, callback);
	}

	public static final void openBufferedWriter(String uri, boolean append, String charset, ResourcesCallback callback) {
		callback.onStart();
		charset = TextUtils.isEmpty(charset) ? DEFAULT_CHARSET : charset;
		OutputStream outputStream = null;
		OutputStreamWriter outputStreamWriter = null;
		BufferedWriter br = null;
		try {
			outputStream = new FileOutputStream(uri, append);
			outputStreamWriter = new OutputStreamWriter(outputStream, charset);
			br = new BufferedWriter(outputStreamWriter);
			callback.onSuccess(br);
		} catch (Exception e) {
			callback.onError(e);
		} finally {
			close(callback, br, outputStreamWriter, outputStream);
		}
		callback.onFinish();
	}

	public static final void openInputStream(String uri, ResourcesCallback callback) {
		openInputStream(uri, null, callback);
	}

	public static final void openInputStream(String uri, Locale locale, ResourcesCallback callback) {
		callback.onStart();
		InputStream inputStream = null;
		try {
			inputStream = getInputStream(uri, locale);
			callback.onSuccess(inputStream);
		} catch (Exception e) {
			callback.onError(e);
		} finally {
			close(callback, inputStream);
		}
		callback.onFinish();
	}

	public static final InputStream getInputStream(String uri) throws IOException {
		return getInputStream(uri, null);
	}

	public static final InputStream getInputStream(String uri, Locale locale) throws IOException {
		InputStream inputStream = null;
		if (tcpIp(uri)) {
			URL url = new URL(uri);
			inputStream = url.openStream();
		} else {
			uri = i18nUriName(uri, locale);
			if (fromClassPath(uri)) {
				uri = classpathUri(uri);
				inputStream = Resources.class.getResourceAsStream(uri);
			} else {
				inputStream = new FileInputStream(uri);
			}
		}
		return inputStream;
	}

	public static void openOutputStream(String uri, boolean append, ResourcesCallback callback) {
		callback.onStart();
		OutputStream os = null;
		try {
			os = new FileOutputStream(uri, append);
			callback.onSuccess(os);
		} catch (Exception e) {
			callback.onError(e);
		} finally {
			close(callback, os);
		}
		callback.onFinish();
	}

	public static final void openInputStreamReader(String uri, ResourcesCallback callback) {
		openInputStreamReader(uri, null, null, callback);
	}

	public static final void openInputStreamReader(String uri, Locale locale, String charset, ResourcesCallback callback) {
		callback.onStart();
		charset = TextUtils.isEmpty(charset) ? DEFAULT_CHARSET : charset;
		InputStream inputStream = null;
		InputStreamReader reader = null;
		try {
			inputStream = getInputStream(uri, locale);
			reader = new InputStreamReader(inputStream, charset);
			callback.onSuccess(reader);
		} catch (Exception e) {
			callback.onError(e);
		} finally {
			close(callback, reader, inputStream);
		}
		callback.onFinish();
	}

	public static boolean tcpIp(String uri) {
		return TCP_IP_PROTOCAL.matcher(uri).find();
	}

	public static boolean fromClassPath(String uri) {
		return uri.startsWith("classpath:");
	}

	public static String classpathUri(String uri) {
		if (uri.charAt(10) == '/') {
			return uri.substring(10);
		}
		return "/" + uri.substring(10);
	}

	public static String i18nUriName(String uri, Locale locale) {
		if (locale != null) {
			int indexOfDot = uri.lastIndexOf(".");
			String local = locale.getLanguage() + "_" + locale.getCountry();
			if (indexOfDot != -1) {
				uri = uri.substring(0, indexOfDot) + "_" + local + uri.substring(indexOfDot);
			} else {
				uri = uri + "_" + local;
			}
		}
		return uri;
	}

	public static void close(ResourcesCallback callback, Closeable...ios) {
		for (Closeable io : ios) {
			if (io != null) {
				try {
					io.close();
				} catch (IOException e) {
					if (callback != null) {
						callback.onCloseFail(e);
					} else {
						throw new RuntimeException("fail to close io", e);
					}
				}
			}
		}
	}

}
