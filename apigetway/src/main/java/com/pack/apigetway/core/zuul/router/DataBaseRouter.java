package com.pacific.apigetway.core.zuul.router;

import com.pacific.apigetway.core.zuul.entity.BasicRoute;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;

import java.util.List;

/**
 * @author yekai
 * @date 20181210
 **/
public class DataBaseRouter extends AbstractDynamicRouter {

    public DataBaseRouter(String servletPath, ZuulProperties properties) {
        super(servletPath, properties);
    }

    @Override
    protected List<BasicRoute> readRoutes() {
        return null;
    }
}
