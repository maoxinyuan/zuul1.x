package com.pacific.apigetway.common.model;

import lombok.Data;

import java.io.Serializable;

/**
 * \* Created with IntelliJ IDEA.
 * \* @author: yanfa
 * \* @date: 2018/6/6
 * \* @description: 申请token接口返回vo
 * \
 */
@Data
public class AuthTokenIssueVo implements Serializable{

    private String token;
    private AuthUserInfoVo userinfo;
    private String salaryMac;
}
