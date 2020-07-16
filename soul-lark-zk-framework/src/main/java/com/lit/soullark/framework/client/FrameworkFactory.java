package com.lit.soullark.framework.client;

import com.lit.soullark.framework.enums.ACLType;
import com.lit.soullark.framework.model.CuratorFrameworkProperties;
import com.lit.soullark.framework.utils.FrameworkThreadFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.framework.imps.DefaultACLProvider;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.ACL;

import java.util.List;

/**
 * @Author myou
 * @Date 2020/7/2  10:35 上午
 */
public class FrameworkFactory {
    /**
     * 创建简单连接实例
     *
     * @param connectionString 连接字符串
     * @param baseSleepTimeMs  每次重试间隔时间(ms)
     * @param retries          最大重试次数
     * @return
     */
    public static CuratorFramework creatSimple(String connectionString, int baseSleepTimeMs, int retries) {
        assert !StringUtils.isBlank(connectionString) : "connectionString can not be null";
        assert !(baseSleepTimeMs < 0) : "baseSleepTimeMs illegal setting";
        assert !(retries < 0) : "retries illegal setting";
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(baseSleepTimeMs, retries);
        return CuratorFrameworkFactory.newClient(connectionString, retryPolicy);
    }

    /**
     * @param properties 连接属性
     * @param policy     重试策略
     * @return
     */
    public static CuratorFramework createWithPolicyAndACL(CuratorFrameworkProperties properties, RetryPolicy policy, ACLProvider aclProvider) {
        assert null != properties : "CuratorFrameworkProperties can not be null";
        if (null == policy)
            policy = new ExponentialBackoffRetry(2000, 3);
        if (null == aclProvider)
            aclProvider = new DefaultACLProvider();
        return FrameworkFactory.createWithOptions(properties)
                .retryPolicy(policy)
                .aclProvider(aclProvider)
                .build();
    }

    /**
     * 支持builder二次定制适配(暂未开发)
     */
    public static CuratorFrameworkFactory.Builder createWithOptions(CuratorFrameworkProperties properties) {
        byte[] auth = null;
        if (properties.getAclType() == ACLType.DIGEST) {
            auth = properties.getUserAndPass().getBytes();
        } else if (properties.getAclType() == ACLType.IP) {
            auth = properties.getIps().getBytes();
        }
        return CuratorFrameworkFactory.builder()
                .threadFactory(new FrameworkThreadFactory())
                .connectString(properties.getCuratorFrameworkConnectionString())
                .connectionTimeoutMs(properties.getCuratorFrameworkConnectTimeoutMs())
                .sessionTimeoutMs(properties.getCuratorFrameworkSessionTimeoutMs())
                .authorization(properties.getAclType().getType(), auth)
                ;
    }
}
