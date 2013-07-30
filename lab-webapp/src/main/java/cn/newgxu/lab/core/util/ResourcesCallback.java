/*


 */
package cn.newgxu.lab.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Properties;

/**
 * 读取资源的工具类，采用回调函数的方式，使用者只需要重载自己想要的回调函数，系统会自动释放打开的资源。
 * 当然，自己在关掉也可以。异常处理也不需要自己手动处理，想要处理的话可以重载当出现异常时的方法。
 * @author longkai(龙凯)
 * @since  13-7-30
 * @email  im.longkai@gmail.com
 * @version 0.1
 */
public class ResourcesCallback {

	private static final Logger L = LoggerFactory.getLogger(ResourcesCallback.class);

	public void onStart() {
		L.info("开始读写数据...");
	}

	public void onSuccess(Properties props) {}

	public void onSuccess(BufferedReader br) throws IOException {}

	public void onError(Throwable t) {
		L.error("读写io时出错！", t);
	}

	public void onCloseFail(Throwable t) {
		L.error("关闭io时出错！", t);
	}

	public void onFinish() {
		L.info("完成数据读写...");
	}

}
