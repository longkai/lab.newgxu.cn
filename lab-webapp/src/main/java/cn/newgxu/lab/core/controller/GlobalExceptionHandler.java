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
package cn.newgxu.lab.core.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.newgxu.lab.core.common.AjaxConstants;

/**
 * 全局的异常处理器，采用json写错误信息。
 * 
 * @author longkai
 * @email im.longkai@gmail.com
 * @since 2013-3-29
 * @version 0.1
 */
@ControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger L = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	@ExceptionHandler(Throwable.class)
	@ResponseBody
	public void exp(Throwable t, HttpServletResponse response) throws IOException {
		L.error("异常处理中。。。", t);
		JSONObject json = new JSONObject();
		try {
			json.put(AjaxConstants.AJAX_STATUS, "no");
			json.put(AjaxConstants.AJAX_MESSAGE, t.getMessage());
			if (t.getCause() != null) {
				json.put(AjaxConstants.EXP_REASON, t.getCause().getMessage());
			} else {
				json.put(AjaxConstants.EXP_REASON, "我们没有能收集足够的错误信息，请您稍后再试！");
			}
		} catch (JSONException e) {
			L.error("异常处理时出错！", e);
		}
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json.toString());
	}
	
}
