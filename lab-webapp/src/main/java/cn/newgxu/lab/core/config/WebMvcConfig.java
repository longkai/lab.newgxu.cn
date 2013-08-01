/*
 * The MIT License (MIT)
 * Copyright (c) 2013 longkai(龙凯)
 */

package cn.newgxu.lab.core.config;

import cn.newgxu.lab.core.util.Resources;
import cn.newgxu.lab.core.util.ResourcesCallback;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

/**
 * @author longkai
 * @email im.longkai@gmail.com
 * @since 13-8-1
 * @version 0.1.0.13-8-1
 */
@Configuration
@EnableWebMvc
//@Import(SpringBeans.class)
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

}
