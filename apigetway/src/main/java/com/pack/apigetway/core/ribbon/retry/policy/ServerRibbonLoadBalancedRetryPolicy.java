package com.pacific.apigetway.core.ribbon.retry.policy;

import com.netflix.client.config.IClientConfig;
import org.springframework.cloud.client.loadbalancer.LoadBalancedRetryContext;
import org.springframework.cloud.client.loadbalancer.ServiceInstanceChooser;
import org.springframework.cloud.netflix.ribbon.RibbonLoadBalancedRetryPolicy;
import org.springframework.cloud.netflix.ribbon.RibbonLoadBalancerContext;

/**
 * @author yekai
 * @date 20181210
 **/
@SuppressWarnings("ALL")
public class ServerRibbonLoadBalancedRetryPolicy extends RibbonLoadBalancedRetryPolicy {

    private RetryTrigger trigger;
    public ServerRibbonLoadBalancedRetryPolicy(String serviceId, RibbonLoadBalancerContext context, ServiceInstanceChooser loadBalanceChooser, IClientConfig clientConfig) {
        super(serviceId, context, loadBalanceChooser, clientConfig);
    }

    public void setTrigger(RetryTrigger trigger) {
        this.trigger = trigger;
    }

    @Override
    public boolean canRetryNextServer(LoadBalancedRetryContext context) {
        boolean retryEnable = super.canRetryNextServer(context);
        if (retryEnable && trigger != null) {
            trigger.exec(context);
        }
        return retryEnable;
    }


    @FunctionalInterface
    public interface RetryTrigger {
        /**
         * 执行重试
         * @param context
         */
        void exec(LoadBalancedRetryContext context);
    }
}
