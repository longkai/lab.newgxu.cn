/*
 * The MIT License (MIT)
 * Copyright (c) 2013 longkai(龙凯)
 */

package cn.newgxu.lab.core.util;

import org.junit.*;

import javax.json.JsonValue;
import java.io.*;
import java.util.Properties;
import java.util.Scanner;

/**
 * @author longkai
 * @version 0.1.0.13-8-2
 * @email im.longkai@gmail.com
 * @since 13-8-2
 */
public class ResourcesTest {

	@Test
	public void testReadJson() throws Exception {
		String uri = "classpath:db.json";
		Resources.readJson(uri, new ResourcesCallback() {
			@Override
			protected void onSuccess(JsonValue json) {
				System.out.println(json);
				org.junit.Assert.assertNotNull(json);
			}
		});
	}

	@Test
	public void testLoadProps() throws Exception {
//		 because we use json to config, so this no use any more
//		 Resources.loadProps("classpath:/config/db.properties", new ResourcesCallback() {
//			 @Override
//			 protected void onSuccess(Properties props) {
//				 org.junit.Assert.assertNotNull(props.getProperty("db.username"));
//			 }
//		 });
	}

	@Test
	public void testOpenBufferedReader() throws Exception {
		Resources.openBufferedReader("http://lab.newgxu.cn", new ResourcesCallback() {
			@Override
			protected void onSuccess(BufferedReader br) throws IOException {
				Scanner in = new Scanner(br).useDelimiter("\\A");
				String html = in.hasNext() ? in.next() : null;
				if (html != null) {
					org.junit.Assert.assertTrue(html.indexOf("<!DOCTYPE html>") != -1);
				}
			}
		});
	}

	@Test
	public void testOpenBufferedWriter() throws Exception {
		final String uri = "D:/test.txt";
		Resources.openBufferedWriter(uri, true, new ResourcesCallback() {
			@Override
			protected void onSuccess(BufferedWriter br) throws IOException {
				br.write(uri);
				br.flush();
				InputStream in = Resources.getInputStream(uri);
				org.junit.Assert.assertNotNull(in);
				in.close();
			}
		});
	}

	@Test
	public void testOpenInputStream() throws Exception {
		Resources.openInputStream("D:/test.txt", new ResourcesCallback() {
			@Override
			protected void onSuccess(InputStream inputStream) throws IOException {
				System.out.println(inputStream.toString());
			}
		});

	}
}
