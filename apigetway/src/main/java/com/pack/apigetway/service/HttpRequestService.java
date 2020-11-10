package com.pacific.apigetway.service;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author yekai
 * @date 2019/1/18 09:43
 * 0.2通过网关的负载post请求
 */
@Service
public class HttpRequestService {

    private final static Logger LOGGER = LoggerFactory.getLogger(HttpRequestService.class);
    private static final String FLAG = "3";

    @Autowired
    private RestTemplate restTemplate;
    private static final String address = "http://";

    /**
     * @param uri  请求路径（端口后面一段路径 ，如: http:127.0.0.1:8080/aaa/bbb，uri就是aaa/bbb，注意前面不要加"/"）
     * @param data  请求参数
     * @param responseType  返回值类型
     * @param token  token
     * @param flag  请求标志（app、web）
     * @param <T>
     * @return
     */
    public  <T> T post(String serviceId,String uri, Object data, Class<T> responseType,String token,String flag,String companyflag){
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept-Charset", MediaType.APPLICATION_JSON.toString());
        headers.add("token",token);
        headers.add("companyflag",companyflag);
        if (StringUtils.isNotBlank(flag)){
            headers.add("flag",flag);
        }else{
            headers.add("flag", FLAG);
        }
        HttpEntity<Object> object = new HttpEntity<>(data, headers);
        String url = address+serviceId+uri;
        LOGGER.info("post request url:{},token:{}",url,token);
        ResponseEntity<T> responseEntity =
                restTemplate.postForEntity(url, object, responseType);
        return responseEntity.getBody();
    }
}
