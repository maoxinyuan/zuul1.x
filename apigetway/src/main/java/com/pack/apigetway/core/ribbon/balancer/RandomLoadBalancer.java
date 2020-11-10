package com.pacific.apigetway.core.ribbon.balancer;

import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;

import java.util.List;
import java.util.Map;

/**
 * @author yekai
 * @date 20181210
 **/
public class RandomLoadBalancer extends AbstractLoadBalancer {
    public RandomLoadBalancer(String name, Map<String, List<String>> unreachableServer) {
        super(name, unreachableServer);
    }

    @Override
    public Server choose(ILoadBalancer loadBalancer) {
        return null;
    }
}
