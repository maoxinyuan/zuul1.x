package com.pacific.apigetway.core.ribbon.balancer;

import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;

/**
 * @author maoxy
 * @date 20181210
 **/
public interface LoadBalancer {

    /**
     * choose a loadBalancer
     * @param loadBalancer
     * @return
     */
    Server chooseServer(ILoadBalancer loadBalancer);
}
