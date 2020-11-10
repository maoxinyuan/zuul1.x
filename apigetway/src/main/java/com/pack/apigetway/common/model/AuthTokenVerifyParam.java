package com.pacific.apigetway.common.model;

import lombok.Data;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: xuewei
 * \* Date: 2018/5/31
 * \* Time: 11:32
 * \* Description:
 * \
 */
public class AuthTokenVerifyParam {
    private String token;

    private Integer requestType = 0;

    public AuthTokenVerifyParam(String token, Integer requestType) {
        this.token = token;
        this.requestType = requestType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getRequestType() {
        return requestType;
    }

    public void setRequestType(Integer requestType) {
        this.requestType = requestType;
    }
}
