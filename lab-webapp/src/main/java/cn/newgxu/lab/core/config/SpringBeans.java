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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
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
 * @version 0.2.0.20130730
 */
@Configuration
@ComponentScan("cn.newgxu.lab.apps")
@EnableTransactionManagement
//@EnableWebMvc // 假如不在web容器上测试的话，那么请注释掉此注解！
public class SpringBeans /*extends WebMvcConfigurerAdapter*/ {
	
	private static final Logger L = LoggerFactory.getLogger(SpringBeans.class);

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
		bean.setTypeAliasesPackage("cn.newgxu.lab.apps.notty.entity");
		return bean;
	}

//	for some reason, we cannot use java config to config mybatis,
//	please refer to http://stackoverflow.com/questions/8999597
//	@Bean
//	public MapperScannerConfigurer mapperScannerConfigurer() {
//		MapperScannerConfigurer configurer = new MapperScannerConfigurer();
//		configurer.setBasePackage("cn.newgxu.lab.apps.notty.repository");
//		return configurer;
//	}
	
	@Bean
	public PlatformTransactionManager tx() {
		return new DataSourceTransactionManager(dataSource());
	}
	
}
