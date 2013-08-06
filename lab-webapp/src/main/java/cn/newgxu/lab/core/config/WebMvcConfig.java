/*
 * The MIT License (MIT)
 * Copyright (c) 2013 longkai(龙凯)
 */

package cn.newgxu.lab.core.config;

import cn.newgxu.lab.core.util.Resources;
import cn.newgxu.lab.core.util.ResourcesCallback;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;
import org.springframework.web.accept.PathExtensionContentNegotiationStrategy;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author longkai
 * @email im.longkai@gmail.com
 * @since 13-8-1
 * @version 0.1.0.13-8-1
 */
@Configuration
@Import(SpringBeans.class)
@EnableWebMvc
public class WebMvcConfig extends WebMvcConfigurerAdapter {

	private static Logger L = LoggerFactory.getLogger(WebMvcConfig.class);

	@Inject
	private ApplicationContext ctx;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		int cachePeriod = 3600 * 24 * 15;
		registry.addResourceHandler("/resources/**").addResourceLocations("/resources/").setCachePeriod(cachePeriod);
		registry.addResourceHandler("/favicon.ico").addResourceLocations("/").setCachePeriod(cachePeriod);
		registry.addResourceHandler("/robots.txt").addResourceLocations("/").setCachePeriod(cachePeriod);
	}

	@Override
	public void addViewControllers(final ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("index");
		registry.addViewController("/index").setViewName("index");
		registry.addViewController("/home").setViewName("index");

		Resources.openInputStream("classpath:uri.json", new ResourcesCallback() {
			@Override
			protected void onSuccess(InputStream inputStream) throws IOException {
				JsonParser parser = Json.createParser(inputStream);
				String url = null;
				String res = null;
				int i = 0;
				while (parser.hasNext()) {
					switch (parser.next()) {
					case KEY_NAME:
						url = parser.getString();
						i++;
						break;
					case VALUE_STRING:
						res = parser.getString();
						i++;
						break;
					default:
						break;
					}
					if (i != 0 && i % 2 == 0) {
						registry.addViewController(url).setViewName(res);
					}
				}
			}
		});
	}

	@Override
	public void configureContentNegotiation(
			ContentNegotiationConfigurer configurer) {
		configurer.favorPathExtension(true).favorParameter(false).ignoreAcceptHeader(false);
	}

	@Bean
	public ViewResolver viewResolver() {
		FreeMarkerViewResolver viewResolver = new FreeMarkerViewResolver();
		viewResolver.setCache(true);
		viewResolver.setPrefix("");
		viewResolver.setSuffix(".html");
		viewResolver.setContentType("text/html;charset=utf-8");
		viewResolver.setRequestContextAttribute("request");
		viewResolver.setExposeSpringMacroHelpers(true);
		viewResolver.setExposeRequestAttributes(true);
		viewResolver.setExposeSessionAttributes(true);
		return viewResolver;
	}

	@Bean
	public FreeMarkerConfig freeMarkerConfig() {
		final FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
		Resources.openInputStream("classpath:freemarker.json", new ResourcesCallback() {
			@Override
			protected void onSuccess(InputStream inputStream) throws IOException {
				JsonReader reader = Json.createReader(inputStream);
				JsonObject jsonObject = reader.readObject();
				configurer.setTemplateLoaderPath(
					jsonObject.getString("template_loader_path", "/WEB-INF/views/"));

				Properties settings = new Properties();
				for (String key : jsonObject.keySet()) {
					if (key.equals("template_loader_path"))
						continue;
					settings.setProperty(key, jsonObject.getString(key));
				}
				configurer.setFreemarkerSettings(settings);
				reader.close();
			}
		});
		return configurer;
	}

	//	文件上传
//	@Bean
//	public MultipartResolver multipartResolver() {
//		return new CommonsMultipartResolver();
//	}

	@Bean
	public StandardServletMultipartResolver servletMultipartResolver() {
		return new StandardServletMultipartResolver();
	}

	@Bean
	public ContentNegotiationManager contentNegotiationManager() {
		Map<String, MediaType> mediaTypes = new HashMap<String, MediaType>(2);
		mediaTypes.put("json", MediaType.APPLICATION_JSON);
		mediaTypes.put("jsonp", MediaType.parseMediaType("application/javascript"));
		PathExtensionContentNegotiationStrategy extension = new PathExtensionContentNegotiationStrategy(mediaTypes);
		HeaderContentNegotiationStrategy header = new HeaderContentNegotiationStrategy();
		ContentNegotiationManager contentNegotiationManager = new ContentNegotiationManager(extension, header);
		return contentNegotiationManager;
	}

	@Bean
	public ContentNegotiatingViewResolver contentNegotiatingViewResolver() {
		ContentNegotiatingViewResolver viewResolver = new ContentNegotiatingViewResolver();
		viewResolver.setOrder(1);
		viewResolver.setContentNegotiationManager(contentNegotiationManager());

		List<View> defaultViews = new ArrayList<View>(2);
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		jsonView.setObjectMapper(ctx.getBean(ObjectMapper.class));
		MappingJackson2JsonpView jsonpView = new MappingJackson2JsonpView();
		jsonpView.setObjectMapper(ctx.getBean(ObjectMapper.class));

		defaultViews.add(jsonView);
		defaultViews.add(jsonpView);
		viewResolver.setDefaultViews(defaultViews);
		return viewResolver;
	}

}
