package com.lit.soullark.framework.configure;

import com.lit.soullark.framework.model.CuratorFrameworkProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @Author myou
 * @Date 2020/7/1  5:29 下午
 */
@Component
@ConditionalOnClass(CuratorFrameworkProperties.class)
@EnableConfigurationProperties(CuratorFrameworkProperties.class)
public class CuratorFrameworkConnectionConfigure {

    private final CuratorFrameworkProperties curatorFrameworkProperties;

    @Autowired
    public CuratorFrameworkConnectionConfigure(CuratorFrameworkProperties curatorFrameworkProperties) {
        this.curatorFrameworkProperties = curatorFrameworkProperties;
    }

    public void setCuratorFrameworkProperties(String curatorFrameworkConnectionString, int retries, int curatorFrameworkConnectTimeoutMs, int curatorFrameworkSessionTimeoutMs, int baseSleepTimeMs) {
        curatorFrameworkProperties.setRetries(retries);
        curatorFrameworkProperties.setCuratorFrameworkConnectionString(curatorFrameworkConnectionString);
        curatorFrameworkProperties.setCuratorFrameworkConnectTimeoutMs(curatorFrameworkConnectTimeoutMs);
        curatorFrameworkProperties.setCuratorFrameworkSessionTimeoutMs(curatorFrameworkSessionTimeoutMs);
        curatorFrameworkProperties.setBaseSleepTimeMs(baseSleepTimeMs);
    }

    @ConditionalOnMissingBean
    public CuratorFrameworkProperties getCuratorFrameworkProperties() {
        return this.curatorFrameworkProperties;
    }
}
