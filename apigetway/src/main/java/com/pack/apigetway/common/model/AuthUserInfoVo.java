package com.pacific.apigetway.common.model;

import java.io.Serializable;

/**
 * \* Created with IntelliJ IDEA.
 * \* @author: yanfa
 * \* @date: 2018/6/6
 * \* @description: 用户信息模型
 * \
 */
public class AuthUserInfoVo implements Serializable{

    private String id;
    private Integer type;
    private String nickName;
    private String account;
    private String dingId;
    private Long mobile;
    //扩展：用户权限，角色，岗位等等

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getDingId() {
        return dingId;
    }

    public void setDingId(String dingId) {
        this.dingId = dingId;
    }

    public Long getMobile() {
        return mobile;
    }

    public void setMobile(Long mobile) {
        this.mobile = mobile;
    }
}

