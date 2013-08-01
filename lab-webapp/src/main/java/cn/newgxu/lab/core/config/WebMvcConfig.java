/*
 * The MIT License (MIT)
 * Copyright (c) 2013 longkai(龙凯)
 */

package cn.newgxu.lab.core.config;

import cn.newgxu.lab.core.util.Resources;
import cn.newgxu.lab.core.util.ResourcesCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;
import org.springframework.web.accept.PathExtensionContentNegotiationStrategy;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

/**
 * @author longkai
 * @email im.longkai@gmail.com
 * @since 13-8-1
 * @version 0.1.0.13-8-1
 */
@Configuration
@EnableWebMvc
public class WebMvcConfig extends WebMvcConfigurerAdapter {

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

		Resources.openBufferedReader("/config/uri", new ResourcesCallback() {

			@Override
			public void onSuccess(BufferedReader br) throws IOException {
				String s = null;
				String[] uris = null;
				while ((s = br.readLine()) != null) {
					uris = s.trim().split(",");
					registry.addViewController(uris[0]).setViewName(uris[1]);
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
		FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
		configurer.setTemplateLoaderPath("/WEB-INF/views/");
		Properties settings = new Properties();
		settings.setProperty("template_update_delay", "0");
		settings.setProperty("default_encoding", "UTF-8");
		settings.setProperty("number_format", "0.##");
		settings.setProperty("datetime_format", "yyyy-MM-dd HH:mm:ss");
		settings.setProperty("classic_compatible", "true");
		settings.setProperty("template_exception_handler", "ignore");
		settings.setProperty("auto_import", "macro.ftl as L");
		configurer.setFreemarkerSettings(settings);
		return configurer;
	}

	//	文件上传
	@Bean
	public MultipartResolver multipartResolver() {
		return new CommonsMultipartResolver();
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
		View jsonView = new MappingJacksonJsonView();
		View jsonpView = new MappingJacksonJsonpView();
		defaultViews.add(jsonView);
		defaultViews.add(jsonpView);
		viewResolver.setDefaultViews(defaultViews);
		return viewResolver;
	}

}
