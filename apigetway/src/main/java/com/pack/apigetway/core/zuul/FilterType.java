package com.pacific.apigetway.core.zuul;

/**
 * @author maoxy
 * @date 20181210
 **/
public enum FilterType {

    /**
     * 前置过滤器
     */
    pre,
    /**
     * 后置过滤器
     */
    post,
    /**
     * 错误过滤器
     */
    error,
    /**
     * 路由过滤器
     */
    routing
}
