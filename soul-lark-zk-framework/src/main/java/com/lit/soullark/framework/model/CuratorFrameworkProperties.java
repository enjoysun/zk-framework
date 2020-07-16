package com.lit.soullark.framework.model;


import com.lit.soullark.framework.enums.ACLType;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author myou
 * @Date 2020/7/1  5:24 下午
 */
@ConfigurationProperties(prefix = "soul.lark.zk.framework")
public class CuratorFrameworkProperties {
    public String getCuratorFrameworkConnectionString() {
        return curatorFrameworkConnectionString;
    }

    public void setCuratorFrameworkConnectionString(String curatorFrameworkConnectionString) {
        this.curatorFrameworkConnectionString = curatorFrameworkConnectionString;
    }

    public int getCuratorFrameworkConnectTimeoutMs() {
        return curatorFrameworkConnectTimeoutMs;
    }

    public void setCuratorFrameworkConnectTimeoutMs(int curatorFrameworkConnectTimeoutMs) {
        this.curatorFrameworkConnectTimeoutMs = curatorFrameworkConnectTimeoutMs;
    }

    public int getCuratorFrameworkSessionTimeoutMs() {
        return curatorFrameworkSessionTimeoutMs;
    }

    public void setCuratorFrameworkSessionTimeoutMs(int curatorFrameworkSessionTimeoutMs) {
        this.curatorFrameworkSessionTimeoutMs = curatorFrameworkSessionTimeoutMs;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public int getBaseSleepTimeMs() {
        return baseSleepTimeMs;
    }

    public void setBaseSleepTimeMs(int baseSleepTimeMs) {
        this.baseSleepTimeMs = baseSleepTimeMs;
    }

    /**
     * 连接字符串
     */
    private String curatorFrameworkConnectionString = "127.0.0.1:2181";
    /**
     * 连接超时时间(ms)
     */
    private int curatorFrameworkConnectTimeoutMs = 1000;
    /**
     * 会话保持时间(ms)
     */
    private int curatorFrameworkSessionTimeoutMs = 1000;
    /**
     * 连接失败重试次数
     */
    private int retries = 3;
    /**
     * 每次重试间隔时间
     */
    private int baseSleepTimeMs = 1000;

    public ACLType getAclType() {
        return aclType;
    }

    public void setAclType(ACLType aclType) {
        this.aclType = aclType;
    }

    public String getUserAndPass() {
        return userAndPass;
    }

    public void setUserAndPass(String userAndPass) {
        this.userAndPass = userAndPass;
    }

    public String getIps() {
        return ips;
    }

    public void setIps(String ips) {
        this.ips = ips;
    }

    /**
     * ACL类型
     */
    private ACLType aclType = ACLType.WORLD;
    /**
     * digest
     * */
    private String userAndPass = "";

    /**
     * ip
     * */
    private String ips = "";
}
