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
