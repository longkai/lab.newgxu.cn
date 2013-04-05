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

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

/**
 * 
 * @author longkai
 * @email im.longkai@gmail.com
 * @since 2013-4-4
 * @version 0.1
 */
public class RegexUtilsTest {

	@Test
	public void testUploadable() {
		String file1 = "hello/t.txt";
		String file2 = "你好.DOC";
		String file3 = "ps.hello.psd";
		String file4 = "test.js.jpeg";
		
		assertThat(RegexUtils.uploadable(file1), is(true));
		assertThat(RegexUtils.uploadable(file2), is(true));
		assertThat(RegexUtils.uploadable(file3), is(false));
		assertThat(RegexUtils.uploadable(file4), is(true));
	}
	
	@Test
	public void testFileExt() {
		String f1 = "/resources/images/hello.jpg";
		String ext1 = RegexUtils.getFileExt(f1);
		assertThat(ext1, is(".jpg"));
		String f2 = "hello";
		String ext2 = RegexUtils.getFileExt(f2);
		assertThat(ext2, is(""));
	}

}
