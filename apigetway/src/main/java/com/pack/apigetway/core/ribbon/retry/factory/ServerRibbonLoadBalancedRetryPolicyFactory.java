package com.pacific.apigetway.core.ribbon.retry.factory;

import com.pacific.apigetway.core.ribbon.retry.policy.ServerRibbonLoadBalancedRetryPolicy;
import org.springframework.cloud.client.loadbalancer.LoadBalancedRetryPolicy;
import org.springframework.cloud.client.loadbalancer.ServiceInstanceChooser;
import org.springframework.cloud.netflix.ribbon.RibbonLoadBalancedRetryPolicyFactory;
import org.springframework.cloud.netflix.ribbon.RibbonLoadBalancerContext;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;

/**
 * @author maoxy
 * @date 20181210
 **/
public class ServerRibbonLoadBalancedRetryPolicyFactory extends RibbonLoadBalancedRetryPolicyFactory {
    private SpringClientFactory clientFactory;
    private ServerRibbonLoadBalancedRetryPolicy policy;
    private ServerRibbonLoadBalancedRetryPolicy.RetryTrigger trigger;

    public ServerRibbonLoadBalancedRetryPolicyFactory(SpringClientFactory clientFactory) {
        super(clientFactory);
        this.clientFactory = clientFactory;
    }

    @Override
    public LoadBalancedRetryPolicy create(String serviceId, ServiceInstanceChooser loadBalanceChooser) {
        RibbonLoadBalancerContext lbContext = this.clientFactory
                .getLoadBalancerContext(serviceId);
        policy = new ServerRibbonLoadBalancedRetryPolicy(serviceId, lbContext, loadBalanceChooser, clientFactory.getClientConfig(serviceId));
        policy.setTrigger(trigger);
        return policy;
    }

    public void setTrigger(ServerRibbonLoadBalancedRetryPolicy.RetryTrigger trigger) {
        policy.setTrigger(trigger);
        this.trigger = trigger;
    }
}
