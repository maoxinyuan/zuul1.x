package com.pacific.apigetway.core.zuul.filter.pre;

import com.pacific.apigetway.core.zuul.FilterType;
import com.pacific.apigetway.core.zuul.filter.AbstractZuulFilter;

/**
 * @author yekai
 * @date 20181210
 * 定义preFilter的抽象类
 **/
public abstract class AbstractPreZuulFilter extends AbstractZuulFilter {
    @Override
    public String filterType() {
        return FilterType.pre.name();
    }
}
