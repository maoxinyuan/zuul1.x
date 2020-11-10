package com.pacific.apigetway.core.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.pacific.apigetway.common.util.JsonResult;
import com.pacific.apigetway.core.zuul.ContantValue;

/**
 * @author maoxy
 * @date 20181210
 * 自定义过滤器
 **/
public abstract class AbstractZuulFilter extends ZuulFilter {

    protected RequestContext context;

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        return (boolean) (ctx.getOrDefault(ContantValue.NEXT_FILTER, true));
    }

    @Override
    public Object run() {
        context = RequestContext.getCurrentContext();
        return doRun();
    }

    /**
     * 拦截器要执行的具体操作
     * @return Result<Object>
     */
    public abstract Object doRun();

    public Object fail(Integer code, String message,Object data) {
        context.set(ContantValue.NEXT_FILTER, false);
        context.setSendZuulResponse(false);
        context.getResponse().setContentType("text/html;charset=UTF-8");
        context.setResponseStatusCode(code);
        context.setResponseBody(JsonResult.error(String.valueOf(code),message,data).toJson());
        return null;
    }

    public Object fail(Integer code, String message) {
        context.set(ContantValue.NEXT_FILTER, false);
        context.setSendZuulResponse(false);
        context.getResponse().setContentType("text/html;charset=UTF-8");
        context.setResponseStatusCode(code);
        context.setResponseBody(JsonResult.error(String.valueOf(code),message).toJson());
        return null;
    }

    public Object fail(String code, String message) {
        context.set(ContantValue.NEXT_FILTER, false);
        context.setSendZuulResponse(false);
        context.getResponse().setContentType("text/html;charset=UTF-8");
        context.setResponseStatusCode(Integer.valueOf(code));
        context.setResponseBody(JsonResult.error(code,message).toJson());
        return null;
    }

    public Object success() {
        context.set(ContantValue.NEXT_FILTER, true);
        return null;
    }
}
