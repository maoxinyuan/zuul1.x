package com.pacific.apigetway.core.zuul.filter.post;

import com.alibaba.fastjson.JSONObject;
import com.pacific.apigetway.common.Constant;
import com.pacific.apigetway.common.redis.BaseRedisDao;
import com.pacific.apigetway.common.util.JsonResult;
import com.pacific.apigetway.common.util.JsonUtil;
import com.pacific.apigetway.common.util.TwStreamUtils;
import com.pacific.apigetway.core.zuul.FilterOrder;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StreamUtils;
import redis.clients.jedis.exceptions.JedisConnectionException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * @author yekai
 * @Date: 2018/12/20 17:31
 * @Description:登录后置过滤器
 */
public class LoginPostFilter extends AbstractPostZuulFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginPostFilter.class);

    @Value("${rsp.login.uri}")
    private String loginUri;
    @Value("${rsp.change.account.uri}")
    private String changeAccountUri;
    @Value("${dingtalk.login.uri}")
    private String dingtalkLoginUri;

    @Autowired
    private BaseRedisDao redisDao;

    private static final Long WEB_TOKEN_EXIIRE_MILLES = 604800L;

    @Override
    public Object doRun() {
        HttpServletRequest request = context.getRequest();
        String uri = request.getRequestURI();
        LOGGER.info("post filter request uri:{}",uri);
        String token = null;
        if (loginUri.equals(uri)||changeAccountUri.equals(uri)||dingtalkLoginUri.equals(uri)){
            InputStream dataStream = context.getResponseDataStream();
            try {
                String body = StreamUtils.copyToString(dataStream, Charset.forName("UTF-8"));
                LOGGER.info("response:{}",body);
                if (StringUtils.isBlank(body)){
                    LOGGER.info("post filter fail response body is blank");
//                    return fail(401,"responseBody is blank");
                }
                JsonResult jsonResult = JSONObject.parseObject(body, JsonResult.class);
                if (jsonResult.isSuccess()){
                    if (dingtalkLoginUri.equals(uri)){
                        token = (String) jsonResult.getData();
                    }else{
                        Map<String, Object> dataMap = JsonUtil.jsontoMap((JSONObject) jsonResult.getData());
                        token = (String)dataMap.get("token");
                    }
                    if (StringUtils.isEmpty(token)){
                        LOGGER.info("post filter fail response token is blank");
//                        return fail(401,"token is null in responseBody");
                    } else {
                        //存到redis中
                        redisDao.set(token,token,WEB_TOKEN_EXIIRE_MILLES);
                    }
                }
                context.setResponseBody(body);
            } catch (IOException e) {
                LOGGER.info(e.getMessage());
//                return fail(33,e.getMessage());
            } catch (JedisConnectionException jce){
                // add on 20191205 by niuchang
                // add redis master slave config, in this case, if the master shutdown, switch new master needs some time
                // so catch JedisConnectionException and returns a result friendly until switch new master is done
                LOGGER.info(jce.getMessage());
                return fail(Constant.REDIS_CONN_FAIL_CODE, Constant.REDIS_CONN_FAIL_MSG);
            }
        }
        LOGGER.info("post filter success response uri:{}",uri);
        return success();
    }

    @Override
    public int filterOrder() {
        return FilterOrder.LOGIN_POST_ORDER;
    }
}
