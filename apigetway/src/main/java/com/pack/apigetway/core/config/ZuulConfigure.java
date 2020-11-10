package com.pacific.apigetway.core.config;

import com.netflix.client.config.CommonClientConfigKey;
import com.netflix.client.config.DefaultClientConfigImpl;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.IRule;
import com.netflix.zuul.ZuulFilter;
import com.pacific.apigetway.core.hystrix.ProducerFallback;
import com.pacific.apigetway.core.ribbon.ServerLoadBalancerRule;
import com.pacific.apigetway.core.ribbon.retry.factory.ServerRibbonLoadBalancedRetryPolicyFactory;
import com.pacific.apigetway.core.zuul.filter.post.LoginPostFilter;
import com.pacific.apigetway.core.zuul.filter.pre.RateLimiterFilter;
import com.pacific.apigetway.core.zuul.filter.pre.TokenAccessFilter;
import com.pacific.apigetway.core.zuul.filter.pre.UserRightFilter;
import com.pacific.apigetway.core.zuul.router.PropertiesRouter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalancedRetryPolicyFactory;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.servlet.MultipartConfigElement;

/**
 * @author yekai
 * @date 20181210
 **/
@Configuration
public class ZuulConfigure {



    @Autowired
    ZuulProperties zuulProperties;
    @Autowired
    ServerProperties server;
    @Value("${ribbon.client.name:client}")
    private String name;
    @Value("${max_file_size}")
    private String maxFileSize;
    @Value("${max_request_size}")
    private String maxRequestSize;

    public static final int DEFAULT_CONNECT_TIMEOUT = 1000;
    public static final int DEFAULT_READ_TIMEOUT = 1000;

    /**
     * 动态路由
     * @return
     */
    @Bean
    public PropertiesRouter propertiesRouter() {
        return new PropertiesRouter(this.server.getServletPrefix(), this.zuulProperties);
    }

    /**
     * 动态负载
     * @return
     */
    public IRule loadBalance() {
        return new ServerLoadBalancerRule();
    }

    public IClientConfig ribbonClientConfig() {
        DefaultClientConfigImpl config = new DefaultClientConfigImpl();
        config.loadProperties(this.name);
        config.set(CommonClientConfigKey.ConnectTimeout, DEFAULT_CONNECT_TIMEOUT);
        config.set(CommonClientConfigKey.ReadTimeout, DEFAULT_READ_TIMEOUT);
        return config;
    }

    /*@ConditionalOnClass(name = "org.springframework.retry.support.RetryTemplate")*/
    /**
     * 负载重试工厂
     * @param clientFactory
     * @return
     */
    public LoadBalancedRetryPolicyFactory loadBalancedRetryPolicyFactory(SpringClientFactory clientFactory) {
        return new ServerRibbonLoadBalancedRetryPolicyFactory(clientFactory);
    }

    /**
     * user过滤器
     * @return
     */
    public ZuulFilter userFilter() {
        return new UserRightFilter();
    }

    /**
     * 限流过滤器
     * @return
     */
    public ZuulFilter rateLimiterFilter() {
        return new RateLimiterFilter();
    }

    /**
     * token过滤器
     * @return
     */
    @Bean
    public ZuulFilter tokenAccessFilter() {
        return new TokenAccessFilter();
    }

    /**
     * 登录后置过滤器
     * @return
     */
//    @Bean
    public  ZuulFilter loginPostFilter(){
        return new LoginPostFilter();
    }

    @Bean
    public ProducerFallback producerFallback(){
        return  new ProducerFallback();
    }

    /**
     * 文件上传修改文件大小限制
     * @return
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //  单个数据大小
        factory.setMaxFileSize(maxFileSize);
        /// 总上传数据大小
        factory.setMaxRequestSize(maxRequestSize);
        return factory.createMultipartConfig();
    }

    /**
     * 跨域问题
     * @return
     */
    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("HEAD");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("PATCH");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

}
