package com.lit.soullark.framework.configure;

import com.lit.soullark.framework.client.FrameworkFactory;
import com.lit.soullark.framework.model.CuratorFrameworkProperties;
import com.lit.soullark.framework.utils.SpringContextUtil;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.ACLProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @Author myou
 * @Date 2020/7/2  3:14 下午
 */
@Component
@Order(Integer.MIN_VALUE)
@ConditionalOnClass(FrameworkFactory.class)
public class CuratorFrameworkInstanceConfigure {

    @Autowired
    private CuratorFrameworkProperties curatorFrameworkProperties;

    @ConditionalOnMissingBean
    public CuratorFramework getSimpleClient() {
        CuratorFramework simple = FrameworkFactory.creatSimple(curatorFrameworkProperties.getCuratorFrameworkConnectionString(),
                curatorFrameworkProperties.getBaseSleepTimeMs(),
                curatorFrameworkProperties.getRetries());
        simple.start();
        return simple;
    }

    @ConditionalOnMissingBean
    public CuratorFramework getClientWithOptions(ACLProvider aclProvider, RetryPolicy retryPolicy) {
        CuratorFramework withPolicyAndACL = FrameworkFactory.createWithPolicyAndACL(curatorFrameworkProperties, retryPolicy, aclProvider);
        withPolicyAndACL.start();
        return withPolicyAndACL;
    }

    public CuratorFrameworkFactory.Builder getCuratorFrameworkBuilder() {
        return FrameworkFactory.createWithOptions(curatorFrameworkProperties);
    }
}
