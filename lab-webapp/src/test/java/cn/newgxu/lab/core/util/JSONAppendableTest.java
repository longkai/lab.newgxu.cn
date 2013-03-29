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

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

/**
 * 测试一下jsonobject的数据是否允许一个对象之外的叠加。
 * Ok!测试通过！
 * 
 * @author longkai
 * @email im.longkai@gmail.com
 * @since 2013-3-29
 * @version 0.1
 */
public class JSONAppendableTest {

	private JavaBean bean;
	
	@Before
	public void init() {
		bean = new JavaBean();
		bean.setId(1);
		bean.setName("yangshuqi");
	}
	
	@Test
	public void test() throws JSONException {
		JSONObject json = new JSONObject(bean);
		System.out.println(json.toString());
		json.put("msg", "hello");
		System.out.println(json.toString());
	}
	
	public static class JavaBean {
		private int		id;
		private String	name;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

}
