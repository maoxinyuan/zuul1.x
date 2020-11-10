package com.pacific.apigetway.service;

import com.pacific.apigetway.entity.ServerAddress;

import java.util.List;

/**
 * @author maoxy
 * @date 20181210
 */
public interface ServerService {
    /**
     * get alive server
     * @return
     */
    List<ServerAddress> getAvailableServer();
}
