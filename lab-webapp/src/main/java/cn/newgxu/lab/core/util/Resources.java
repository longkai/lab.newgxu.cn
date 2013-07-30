package cn.newgxu.lab.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

/**
 * 读写资源的具体实现，模版方法。
 * @author longkai(龙凯)
 * @email  im.longkai@gmail.com
 * @since  13-7-30
 * @version 0.1
 */
public class Resources {

	private static final Logger L = LoggerFactory.getLogger(Resources.class);

	public static final void loadProps(String path, ResourcesCallback callback) {
		callback.onStart();
		path = fromClassPath(path);
		InputStream in = null;
		try {
			in = Resources.class.getResourceAsStream(path);
			Properties props = new Properties();
			props.load(in);
			callback.onSuccess(props);
		} catch (IOException e) {
			callback.onError(e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					callback.onCloseFail(e);
				}
			}
		}
		callback.onFinish();
	}

	public static final void openBufferedReader(String path, ResourcesCallback callback) {
		openBufferedReader(path, null, callback);
	}

	public static final void openBufferedReader(String path, String charset, ResourcesCallback callback) {
		callback.onStart();
		path = fromClassPath(path);
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		try {
			inputStream = Resources.class.getResourceAsStream(path);
			if (charset != null && charset.length() != 0) {
				inputStreamReader = new InputStreamReader(inputStream, charset);
			} else {
				inputStreamReader = new InputStreamReader(inputStream);
			}
			bufferedReader = new BufferedReader(inputStreamReader);
			callback.onSuccess(bufferedReader);
		} catch (Exception e) {
			callback.onError(e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					callback.onCloseFail(e);
				}
			}
			if (inputStreamReader != null) {
				try {
					inputStreamReader.close();
				} catch (IOException e) {
					callback.onCloseFail(e);
				}
			}
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					callback.onCloseFail(e);
				}
			}
		}
		callback.onFinish();
	}

	private static final String fromClassPath(String path) {
		if (!path.startsWith("classpath:")) {
			return path;
		}
		return "/" + path.substring(10);
	}

}
