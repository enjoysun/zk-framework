#### 特性介绍

###### zk节点自动绑定到Model   

如zk服务中存在/yjcloud/yjcbridge/config/default/oss data={"akId":"LTAINARqI3JMgqkn","akSecret":"rjR4KmgbG4R0aPYMb73Caip2b76t4L","audioBucket":"dispute-api","endpoint":"https://oss-cn-hangzhou.aliyuncs.com"}
想把data映射到Model中，lark-framework能够自动将data绑定到model，如下:
```java
@Data
@LarkBind(value = "oss", watched = true, compensator = Demo.class, isMatchPrefix = true)
public class OssPath {
    private String akId;
    private String akSecret;
    private String audioBucket;
    private String endpoint;
}
``` 

###### 映射Model动态注册到IOC  

lark-framework不仅实现了zkNode和Model自动映射，同时也实现了Model动态注入。更加便捷实用，如下:  

```java
import org.springframework.beans.factory.annotation.Autowired;
public class TemplateTests {
    @Autowired
    private OssPath ossPath;
}
```

###### 节点变化监听和补偿策略   

zk作为分布式协调组件，一般配置文件会产生变动，如果配置文件产生变动，针对该场景，lark-framework实现了节点变动监控开关
如果打开监控开关，则当前节点的任何改动都会触发映射Model的更改并通知给调用者，lark-framework同时提供了变更通知补偿策
略，可以在补偿通知策略中实现节点变动业务处理。 

###### 开放配置类和连接实例工厂  

lark-framework依赖curator组件进行zk访问，在该组件基础上进行高层次API抽象，所以lark-framework提供了用户自定义和配置
文件两种方式录入zk连接配置参数。关于zk连接实例，lark-framework也提供了连接工厂对接这两种配置连接产生实例，同时也提供底
层curator的builder用来构造更富态的连接实例

#### 使用步骤   

**1.引入lark-framework依赖**   

```xml
<dependency>
    <groupId>com.lit.soullark</groupId>
    <artifactId>soul-lark-zk-framework</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```  

**2.配置连接参数**  
  
```text
1.通过配置文件(建议使用)    
soul.lark.zk.framework.curator-framework-connection-string:zk连接地址集群可用逗号分隔  
soul.lark.zk.framework.curator-framework-session-timeout-ms:会话session超时时间(ms)  
soul.lark.zk.framework.curator-framework-connect-timeout-ms:连接超时时间(ms)  
soul.lark.zk.framework.base-sleep-time-ms:连接重试间隔(ms)  
soul.lark.zk.framework.retries:连接重试次数  
soul.lark.zk.framework.mapper:映射Model注册扫描路径,多个路径使用逗号分隔  
soul.lark.zk.framework.userAndPass:ACL账号和密码格式  account:password    
soul.lark.zk.framework.parent-node:节点父目录(如果节点目录过长，可配置父目录，LarkBind开启isMatchPrefix，value中只需要配置子级目录即可实现自动拼接)  
soul.lark.zk.framework.acl-type:auth的模式


2.手动注册连接参数(不推荐,如果需要实现富态连接实例可依赖该方式)  
通过CuratorFrameworkConnectionConfigure.setCuratorFrameworkProperties手动注入  
```  

**3.注册zk连接实例**  

```java
/**
*该类注册一个简单的zk连接实例，如果想自定义重试策略和权限策略可调用getClientWithOptions(ACLProvider aclProvider, RetryPolicy retryPolicy)方法
*注意:为了保证连接实例bean在映射bean属性设置之前实例化完成，所以需要为该配置类设置@Order(Integer.MIN_VALUE)
*/
@Order(Integer.MIN_VALUE)
@Configuration
public class CuratorFrameConfiguration {

    @Autowired
    private CuratorFrameworkInstanceConfigure curatorFrameworkInstanceConfigure;

//    @Autowired
//    private CuratorFrameworkConnectionConfigure curatorFrameworkConnectionConfigure;

    @Bean
    public CuratorFramework curatorFramework() {
//        curatorFrameworkConnectionConfigure.setCuratorFrameworkProperties("192.168.100.161:2181", 3, 2000, 2000, 2000);
        return curatorFrameworkInstanceConfigure.getSimpleClient();
    }
}
```  

**4.定义映射model(注意:model包地址需要指定到配置文件mapper配置,参照mybatis的mapper配置指定)**  

```java
@Data
@LarkBind(value = "oss", watched = true, compensator = Demo.class, isMatchPrefix = true)
public class OssPath {
    // /yjcloud/yjcbridge/config/default/
    private String akId;
    private String akSecret;
    private String audioBucket;
    private String endpoint;
}
```   
 
```text
以上面的model解析:
value:zkNode地址
watched:变化监听开关。如果为true则会进行节点变化监听和通知
isMatchPrefix:前缀开启开关。如果打开则value的值为配置文件中的parent-node+value，如果关闭则value不进行拼接
compensator:补偿策略(当打开变化监听开关时，补偿策略才会开启，该策略用来处理节点变化后业务更新)
```  

```java
/**
* 补偿策略demo
*/
public class Demo implements CompensationStrategyService<OssPath> {
    @Override
    public void compensationDeals(OssPath model) {
        System.out.println(String.format("this is a demo for compensation, you can do something that will get notified when changes occur. %s", model.getEndpoint()));
    }
}
```

#### 依赖介绍  

- 依赖环境  

|  环境 | 版本 |
| -------- | -------- |  
|junit|4.11|  
|curator-recipes|5.0.0|  
|jackson-databind|2.11.1|  
|commons-lang3|3.7|  
|spring-boot|2.0.4|  

#### New Features(next...)  

> 多线程补偿任务处理。自定义线程池进行补偿任务处理，保证zk节点频繁修改情况下，系统处理的健壮性。(关于线程池队列类型的影响考虑:如果一个节点频繁的修改，触发补偿任务，线程池在处理任务补偿时并未进行公平处理且执行周期也不可控，易造成非安全场景，即频繁修改两次，可能第一次结果覆盖第二次结果，针对该场景安全性和性能考虑，可以借鉴ConcurrentHashmap分段锁设计模式，即为每一个zk的node节点作为一个锁标识，相同锁标识的任务，需要同一线程处理或者使用锁来保证其原子性。该场景暂未实现)
