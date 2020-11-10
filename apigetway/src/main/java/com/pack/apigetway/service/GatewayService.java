package com.pacific.apigetway.service;

import com.pacific.apigetway.entity.GatewayAddress;

import java.util.List;

/**
 * @author maoxy
 * @date 20181210
 **/
public interface GatewayService {

    /**
     * 获取网关信息
     * @param host
     * @param port
     * @return
     */
    GatewayAddress getByHostAndPort(String host, Integer port);
}
