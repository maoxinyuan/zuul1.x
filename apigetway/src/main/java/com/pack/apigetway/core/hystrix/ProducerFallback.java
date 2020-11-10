package com.pacific.apigetway.core.hystrix;

import com.alibaba.fastjson.JSONObject;
import com.pacific.apigetway.common.util.JsonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author yekai
 * @date 2019/5/6 14:09
 */
public class ProducerFallback implements FallbackProvider {

    private final Logger LOGGER = LoggerFactory.getLogger(ProducerFallback.class);

    @Override
    public String getRoute() {
        return "*";
    }

    @Override
    public ClientHttpResponse fallbackResponse(Throwable cause) {
        if (cause != null && cause.getCause() != null) {
            String reason = cause.getCause().getMessage();
            LOGGER.info("Excption {}",reason);
        }
        return fallbackResponse();
    }

    @Override
    public ClientHttpResponse fallbackResponse() {
        return new ClientHttpResponse() {
            @Override
            public HttpStatus getStatusCode() throws IOException {
                return HttpStatus.OK;
            }

            @Override
            public int getRawStatusCode() throws IOException {
                return HttpStatus.OK.value();
            }

            @Override
            public String getStatusText() throws IOException {
                return HttpStatus.OK.getReasonPhrase();
            }

            @Override
            public void close() {

            }

            @Override
            public InputStream getBody() throws IOException {

                return new ByteArrayInputStream(jsonString().getBytes());
            }

            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                return headers;
            }
        };
    }

    private static String jsonString(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success",false);
        jsonObject.put("errorCode","500");
        jsonObject.put("businessCode","1");
        jsonObject.put("data","");
        jsonObject.put("msg","服务异常，请稍后重试!");
        return jsonObject.toString();
    }

}
