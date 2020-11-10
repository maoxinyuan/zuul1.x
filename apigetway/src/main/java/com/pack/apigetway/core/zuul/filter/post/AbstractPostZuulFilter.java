package com.pacific.apigetway.core.zuul.filter.post;

import com.pacific.apigetway.core.zuul.FilterType;
import com.pacific.apigetway.core.zuul.filter.AbstractZuulFilter;

/**
 * @author yekai
 * @Date: 2018/12/20 17:30
 * @Description:
 */
public abstract class AbstractPostZuulFilter  extends AbstractZuulFilter {
    @Override
    public String filterType() {
        return FilterType.post.name();
    }
}
