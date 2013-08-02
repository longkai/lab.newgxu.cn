package cn.newgxu.lab.core.util;

import org.junit.*;

import static org.junit.Assert.assertEquals;

/**
 * @author longkai
 * @version 0.1.0.13-8-2
 * @email im.longkai@gmail.com
 * @since 13-8-2
 */
public class TextUtilsTest {

	@Test
	public void testGetter() throws Exception {
		String f1 = "name";
		assertEquals("getName", TextUtils.getter(f1));
		String f2 = "_hi";
		assertEquals("get_hi", TextUtils.getter(f2));
	}

	@Test
	public void testSetter() throws Exception {
		String f1 = "f";
		assertEquals("setF", TextUtils.setter(f1));
	}
}
