package com.pacific.apigetway;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.pacific.apigetway.core.zuul.router.PropertiesRouter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.RoutesRefreshedEvent;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.*;
/**
 * @author maoxy
 */
@EnableAutoConfiguration
@EnableApolloConfig
@EnableZuulProxy
@EnableEurekaClient
@ComponentScan(basePackages = {
		"com.pacific.apigetway.core",
		"com.pacific.apigetway.service",
		"com.pacific.apigetway.common.redis"
})
public class ApigetwayApplication   extends SpringBootServletInitializer implements CommandLineRunner {

	@Autowired
	ApplicationEventPublisher publisher;
	@Autowired
	RouteLocator routeLocator;

	private ScheduledExecutorService executor;
	private boolean instance = false;


	@Bean
	@LoadBalanced
	RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Override
	public void run(String... args) throws Exception {
		executor = newSingleThreadScheduledExecutor(
				new ThreadFactoryBuilder().setNameFormat("properties read.").build()
		);
		executor.scheduleWithFixedDelay(() -> publish(), 0, 1, TimeUnit.MINUTES);
	}

	private void publish() {
		if (isPropertiesModified()) {
			publisher.publishEvent(new RoutesRefreshedEvent(routeLocator));
		}
	}

	/**
	 * 监听Apollo配置是否变化
	 * @return
	 */
	private boolean isPropertiesModified(){
		Config routerConfig = ConfigService.getConfig(PropertiesRouter.ZUUL_ROUTER_NAMESPACE);
		routerConfig.addChangeListener(new ConfigChangeListener() {
			@Override
			public void onChange(ConfigChangeEvent changeEvent) {
				instance = true;
			}
		});
		return instance;
	}

	/**
	 * 需要把web项目打成war包部署到外部tomcat运行时需要改变启动方式
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(ApigetwayApplication.class);
	}


	@Bean
	public HttpMessageConverters fastJsonHttpMessageConverters(){
		//1.需要定义一个convert转换消息的对象;
		FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
		//2:添加fastJson的配置信息;
		FastJsonConfig fastJsonConfig = new FastJsonConfig();
		fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat,SerializerFeature.WriteNullStringAsEmpty);
		//3处理中文乱码问题
		List<MediaType> fastMediaTypes = new ArrayList<>();
		fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
		//4.在convert中添加配置信息.
		fastJsonHttpMessageConverter.setSupportedMediaTypes(fastMediaTypes);
		fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
		HttpMessageConverter<?> converter = fastJsonHttpMessageConverter;
		return new HttpMessageConverters(converter);
	}

	public static void main(String[] args) {
		SpringApplication.run(ApigetwayApplication.class, args);
	}

}



