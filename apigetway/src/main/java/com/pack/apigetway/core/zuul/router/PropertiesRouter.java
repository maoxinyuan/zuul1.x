package com.pacific.apigetway.core.zuul.router;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.google.common.collect.Lists;
import com.pacific.apigetway.common.Context;
import com.pacific.apigetway.core.zuul.entity.BasicRoute;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yekai
 * @Date: 2018/12/19 10:45
 * @Description:动态路由
 */
public class PropertiesRouter extends AbstractDynamicRouter {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesRouter.class);
    public static final String ZUUL_ROUTER_NAMESPACE = "router";
    private static final String ZUUL_ROUTER_PREFIX = "zuul.routes";
    private static final String ZUUL_ROUTER_PATH = "path";
    private static final String ZUUL_ROUTER_SERVICEID = "serviceId";
    private static final String ZUUL_ROUTER_URL = "url";
    private static final String ZUUL_ROUTER_RETRYABLE = "retryable";
    private static final String ZUUL_ROUTER_STRIP_PREFIX = "strip-prefix";
    private static final String SPILT = "/";
    private static final String FALSE = "false";
    private static final String TRUE = "true";


    public PropertiesRouter(String servletPath, ZuulProperties properties) {
        super(servletPath, properties);
    }

    @Override
    protected List<BasicRoute> readRoutes() {
        List<BasicRoute> list = Lists.newArrayListWithExpectedSize(3);
            Config config = ConfigService.getConfig(ZUUL_ROUTER_NAMESPACE);
            Set<String> propertyNames = config.getPropertyNames();
            Map<String,String> configmap =  new HashMap<String,String>(1);
            propertyNames.stream().forEach(propertyName->{
                configmap.put(propertyName,config.getProperty(propertyName,null));
            });

            Context context = new Context(configmap);
            Map<String, String> data = context.getSubProperties(ZUUL_ROUTER_PREFIX);
            List<String> ids = data.keySet().stream().map(s -> s.substring(0, s.indexOf("."))).distinct().collect(Collectors.toList());
            ids.stream().forEach(id -> {
                Map<String, String>  router = context.getSubProperties(String.join(".", ZUUL_ROUTER_PREFIX, id));

                String path = router.get(ZUUL_ROUTER_PATH);
                path = path.startsWith(SPILT) ? path : SPILT + path;

                String serviceId = router.getOrDefault(ZUUL_ROUTER_SERVICEID, null);

                String url = router.getOrDefault(ZUUL_ROUTER_URL, null);

                BasicRoute basicRoute = new BasicRoute();
                basicRoute.setId(id);
                basicRoute.setPath(path);
                if (StringUtils.isNotBlank(url)){
                    //单实例需要这一行，多实例不需要这一行
                    basicRoute.setUrl(url);
                }
                basicRoute.setServiceId((StringUtils.isBlank(url) && StringUtils.isBlank(serviceId)) ? id : serviceId);
                basicRoute.setRetryable(Boolean.parseBoolean(router.getOrDefault(ZUUL_ROUTER_RETRYABLE, FALSE)));
                /**
                 * #当stripPrefix=true的时候 （http://127.0.0.1:8181/api/user/list -> http://192.168.1.100:8080/user/list）
                 * #当stripPrefix=false的时候（http://127.0.0.1:8181/api/user/list -> http://192.168.1.100:8080/api/user/list）
                 */
                basicRoute.setStripPrefix(Boolean.parseBoolean(router.getOrDefault(ZUUL_ROUTER_STRIP_PREFIX, FALSE)));
                list.add(basicRoute);
            });
        return list;
    }
}
/**
 * 配置多实例
 * api.ribbon.listOfServers=192.168.1.100:8080,192.168.1.101:8080,192.168.1.102:8080
 *  不需要配置zuul.routes.apollomall.url
 */

