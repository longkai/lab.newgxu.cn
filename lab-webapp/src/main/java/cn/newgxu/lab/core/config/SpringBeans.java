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
package cn.newgxu.lab.core.config;

import cn.newgxu.lab.core.util.Resources;
import cn.newgxu.lab.core.util.ResourcesCallback;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
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

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

/**
 * spring的beans配置文件。
 * @author longkai
 * @email im.longkai@gmail.com
 * @since 2013-3-28
 * @version 0.1
 */
@Configuration
@ComponentScan("cn.newgxu.lab")
@EnableTransactionManagement
@EnableWebMvc // 假如不在web容器上测试的话，那么请注释掉此注解！
public class SpringBeans extends WebMvcConfigurerAdapter {
	
	private static final Logger L = LoggerFactory.getLogger(SpringBeans.class);

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

	@Bean(destroyMethod = "close")
	public DataSource dataSource() {
		final org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();

		Resources.loadProps("classpath:config/dataSource.properties", new ResourcesCallback() {

			@Override
			public void onSuccess(Properties props) {
				PoolProperties poolProperties = new PoolProperties();

				poolProperties.setUsername(props.getProperty("db.username"));
				poolProperties. setPassword(props.getProperty("db.password"));
				poolProperties.setUrl(props.getProperty("db.url"));
				poolProperties.setDriverClassName(props.getProperty("db.driver"));
				poolProperties.setDefaultAutoCommit(false);
				dataSource.setPoolProperties(poolProperties);
			}
		});
		return dataSource;
	}

	@Bean
	public SqlSessionFactoryBean sqlSessionFactory() {
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
		bean.setDataSource(dataSource());
		bean.setTypeAliasesPackage("cn.newgxu.lab");
		return bean;
	}

	@Bean
	public MapperScannerConfigurer mapperScannerConfigurer() {
		MapperScannerConfigurer configurer = new MapperScannerConfigurer();
		configurer.setBasePackage("cn.newgxu.lab");
		return configurer;
	}
	
//	@Bean
//	public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() {
//		LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
//		entityManagerFactoryBean.setDataSource(dataSource());
//		entityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter());
//
//		Properties properties = new Properties();
//		properties.setProperty("hibernate.hbm2ddl.auto", "none");
////		properties.setProperty("hibernate.hbm2ddl.auto", "update");
//		entityManagerFactoryBean.setJpaProperties(properties);
//
//		properties.clear();
//		InputStream in = null;
//		try {
//			in = this.getClass().getResourceAsStream("/config/entityPackages.properties");
//			properties.load(in);
//		} catch (IOException e) {
//			L.error("启动EntityManagerFactory出错", e);
//		} finally {
//			try {
//				in.close();
//			} catch (IOException e) {
//				L.error("wtf!", e);
//			}
//		}
//		String[] entityPackages = new String[properties.size()];
//		int i = 0;
//		for (Object pkg : properties.keySet()) {
//			entityPackages[i++] = properties.getProperty(pkg.toString());
//		}
//		entityManagerFactoryBean.setPackagesToScan(entityPackages);
//		return entityManagerFactoryBean;
//	}

//	@Bean
//	public JpaVendorAdapter jpaVendorAdapter() {
//		HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
////		jpaVendorAdapter.setShowSql(true);
//		jpaVendorAdapter.setShowSql(false);
//		jpaVendorAdapter.setDatabase(Database.MYSQL);
//		jpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL5InnoDBDialect");
////		这里不管怎么设置，实际上最终还是依赖于jpaProperties的相关设置
//		jpaVendorAdapter.setGenerateDdl(false);
//		return jpaVendorAdapter;
//	}

//	@Bean
//	public PlatformTransactionManager transactionManager() {
//		JpaTransactionManager transactionManager = new JpaTransactionManager();
//		transactionManager.setEntityManagerFactory(entityManagerFactoryBean().getObject());
//		return transactionManager;
//	}
//
//	@Bean
//	public PersistenceAnnotationBeanPostProcessor persistenceAnnotationBeanPostProcessor() {
//		return new PersistenceAnnotationBeanPostProcessor();
//	}

//	@Bean
//	public PersistenceExceptionTranslationPostProcessor exceptionTranslator() {
//		return new PersistenceExceptionTranslationPostProcessor();
//	}

	@Bean
	public PlatformTransactionManager tx() {
		return new DataSourceTransactionManager(dataSource());
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
		CommonsMultipartResolver resolver = new CommonsMultipartResolver();
		return resolver;
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
