package com.lit.soullark.framework.handles.impl;


import com.lit.soullark.framework.annoations.LarkBind;
import com.lit.soullark.framework.client.ACLFactory;
import com.lit.soullark.framework.enums.ACLType;
import com.lit.soullark.framework.handles.CompensationStrategyService;
import com.lit.soullark.framework.utils.SpringContextUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.SetACLBuilder;
import org.apache.zookeeper.data.ACL;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.core.env.Environment;

import java.util.List;

/**
 * @Author myou
 * @Date 2020/7/6  10:23 上午
 */
public class LarkInitializingBeanProcessor extends InstantiationAwareBeanPostProcessorAdapter {

    @Autowired
    private Environment environment;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        LarkBind annotation = bean.getClass().getAnnotation(LarkBind.class);
        if (null != annotation) {
            String path = annotation.value();
//            ACLType type = annotation.type();
            boolean watched = annotation.watched();
            Class<? extends CompensationStrategyService> compensator = annotation.compensator();
            boolean matchPrefix = annotation.isMatchPrefix();
            if (matchPrefix) {
                String property = environment.getProperty("soul.lark.zk.framework.parent-node", String.class, "");
                if (StringUtils.isNotBlank(property))
                    path = String.format("%s/%s", property, path);
            }
            CuratorFramework curatorFramework = SpringContextUtil.getBeanByClass(CuratorFramework.class);
            if (null != curatorFramework) {
                LarkInvocationHandler invocationHandler = new LarkInvocationHandler(curatorFramework, path);
                try {
                    return invocationHandler.bindNodeData(bean.getClass(), watched, compensator);
                } catch (Exception e) {
                    System.out.println(String.format("bean processorerror msg:%s", e.getMessage()));
                    return super.postProcessAfterInstantiation(bean, beanName);
                }
            }
        }
        return super.postProcessAfterInitialization(bean, beanName);
    }
}
