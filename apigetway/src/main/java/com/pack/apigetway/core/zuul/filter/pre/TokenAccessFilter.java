package com.pacific.apigetway.core.zuul.filter.pre;

import com.pacific.apigetway.common.Constant;
import com.pacific.apigetway.common.model.AuthTokenVerifyParam;
import com.pacific.apigetway.common.redis.BaseRedisDao;
import com.pacific.apigetway.common.redis.RedisHandle;
import com.pacific.apigetway.common.util.JsonResult;
import com.pacific.apigetway.core.zuul.FilterOrder;
import com.pacific.apigetway.service.HttpRequestService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import redis.clients.jedis.exceptions.JedisConnectionException;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Enumeration;
import java.util.Map;
import java.util.UUID;

/**
 * @author yekai
 * @date 2018/12/18 17:36
 * token过滤器
 */
public class TokenAccessFilter extends AbstractPreZuulFilter{


    @Autowired
    private HttpRequestService httpRequestService;

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenAccessFilter.class);

    private static final Long WEB_TOKEN_EXIIRE_MILLES = 28800L;
    private static final Long APP_TOKEN_EXIIRE_MILLES = 604800L;
    private static final Long WINPHONE_TOKEN_EXIIRE_MILLES = 3600L;

    //token失效错误码
    private static final Integer   TOKEN_IS_FAIL =401;
    //前提条件失败
    private  static  final  Integer CHECK_IS_FAIL = 412 ;

    //PC端
    private static final String  WEB_FLAG="0";
    //APP端
    private static final String  APP_FLAG="1";
    //winphone
    private static final String  WINPHONE_FLAG="2";
    //server
    private static final String  SERVER_FLAG="3";

    @Value("${rsp.login.uri}")
    private String loginUri;
    @Value("${rsp.change.account.uri}")
    private String changeAccountUri;
    @Value("${dingtalk.login.uri}")
    private String dingtalkLoginUri;
    @Value("${userinfo.checkToken}")
    private String checkTokenUri;
    @Value("${rsp-manager.api.sys.sysUserInfo.updatePersonalPwd}")
    private String updatePersonalPwdUri;
    @Value("${rsp-manager.api.sys.sysUserInfo.getUserInfoByMobileOrAccount}")
    private String getUserInfoByMobileOrAccountUri;
    @Value("${rsp-manager.api.sys.sysUserInfo.getMessageForUpdatePwd}")
    private String getMessageForUpdatePwdUri;
    @Value("${rsp-manager.api.sys.messinfo.checkMobileCode}")
    private String checkMobileCodeUri;
    @Value("${write.uri.list}")
    private String writeUriList;

    @Autowired
    private BaseRedisDao redisDao;

    @Override
    public Object doRun() {
        String uuid = UUID.randomUUID().toString();
        //减少前端跨域接口预检
        context.addZuulRequestHeader("Access-Control-Max-Age", "3600");

        HttpServletRequest request = context.getRequest();

        String uri = request.getRequestURI();
        LOGGER.info("UUID:{},pre filter request uri:{}",uuid,uri);

//        Enumeration<String> headerNames = request.getHeaderNames();
//        while (headerNames.hasMoreElements()){
//            String headName = headerNames.nextElement();
//            String value = request.getHeader(headName);
//            if (StringUtils.isBlank(value)){
//                value = "这是个null";
//            }
//            LOGGER.info("UUID:{},headerValue uri:{},key:{},value:{}",uuid,uri,headName,value);
//        }

        String[] writeUris = writeUriList.split(",");
        for (String writeuri: writeUris) {
            if (uri.equals(writeuri)){
                LOGGER.info("UUID:{},pre filter success request is write shoud not check uri:{}",uuid,uri);
                return success();
            }
        }

        if (loginUri.equals(uri)
                ||changeAccountUri.equals(uri)
                ||dingtalkLoginUri.equals(uri)
                ||checkTokenUri.equals(uri)
                ||updatePersonalPwdUri.equals(uri)
                ||getUserInfoByMobileOrAccountUri.equals(uri)
                ||getMessageForUpdatePwdUri.equals(uri)
                ||checkMobileCodeUri.equals(uri)){
            LOGGER.info("UUID:{},pre filter success request shoud not check uri:{}",uuid,uri);
            return success();
        }

        String flag = request.getHeader("flag");
        LOGGER.info("uri:{},flag:{}",uri,flag);
        if (null == flag||"" == flag ||flag.length()==0){
            LOGGER.info("UUID:{},pre filter fail request flag is empty,uri:{}",uuid,uri);
            return fail(CHECK_IS_FAIL, "flag must be specify.");
        }

        String token = request.getHeader("token");
        LOGGER.info("UUID:{},uri:{},token:{}",uuid,uri,token);
        String companyflag = request.getHeader("companyflag");
        LOGGER.info("UUID:{},uri:{},companyflag:{}",uuid,uri,companyflag);
//        if (StringUtils.isBlank(companyflag)){
//            return fail(CHECK_IS_FAIL,"companyflag must be specify.");
//        }
        switch (flag){
            case WEB_FLAG:
                return checkAndHandle(token,WEB_TOKEN_EXIIRE_MILLES,uri,flag,companyflag,uuid);
            case APP_FLAG:
                return checkAndHandle(token,APP_TOKEN_EXIIRE_MILLES,uri,flag,companyflag,uuid);
            case WINPHONE_FLAG:
                return checkTokenAndHandle(token,WINPHONE_TOKEN_EXIIRE_MILLES,uri,flag,companyflag,uuid);
            case SERVER_FLAG:
                return success();
            default:
                return fail(CHECK_IS_FAIL, "非法客户端");
        }
    }

    @Override
    public int filterOrder() {
        return FilterOrder.TOKEN_ACCESS_ORDER;
    }

    private Object checkAndHandle(String token,Long expireTime,String uri,String flag,String companyflag,String uuid){
        LOGGER.info("UUID:{},logagent-pre-filter-success-request-token:,{}:,uri:,{}:,flag:,{}",uuid,token,uri,flag);
        return checkTokenAndHandle(token,expireTime,uri,flag,companyflag,uuid);
    }


    private Object checkTokenAndHandle(String token,Long expireTime,String uri,String flag,String companyflag,String uuid){
        LOGGER.info("pre filter request token pre CHECK_IS_FAIL check,uri:{},token:{}",uri,token);
        if (null == token||"" == token){
            LOGGER.info("pre filter fail request token is empty,uri:{},token:{},必传参数",uri,token);
            return fail(CHECK_IS_FAIL, "未登陆状态,请重新登陆");
        }
        LOGGER.info("pre filter request token post check CHECK_IS_FAIL,uri:{},token:{}",uri,token);

        Object redisToken;
        try {
            redisToken = redisDao.get(token);
        }catch (JedisConnectionException jce){
            // add on 20191205 by niuchang
            // add redis master slave config, in this case, if the master shutdown, switch new master needs some time
            // so catch JedisConnectionException and returns a result friendly until switch new master is done
            LOGGER.info(jce.getMessage());
            return fail(Constant.REDIS_CONN_FAIL_CODE, Constant.REDIS_CONN_FAIL_MSG);
        }

        if (null != redisToken && "" != redisToken && String.valueOf(redisToken).length()>0){
            LOGGER.info("UUID:{},pre filter success request token:{},uri:{}",uuid,token,uri);
            return success();
        }
        LOGGER.info("UUID:{},pre filter fail token is invalid or overdue,uri:{},token:{}",uuid,uri,token);
//        return fail(TOKEN_IS_FAIL,"登陆过期,请重新登陆!token:"+token+",redisToken:"+redisToken+",flag:"+flag+",uri:"+uri+",uuid:"+uuid);
        return fail(TOKEN_IS_FAIL,"登陆过期,请重新登陆!","token:"+token+",redisToken:"+redisToken+",flag:"+flag+",uri:"+uri+",uuid:"+uuid);
    }

}
