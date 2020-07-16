package com.lit.soullark.framework.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @Author myou
 * @Date 2020/7/1  4:40 下午
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {
    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.context = applicationContext;
    }

    // 获取当前环境
    public static String getActiveProfile() {
        return context.getEnvironment().getActiveProfiles()[0];
    }

    // 获取当前环境(多个)
    public static String[] getActiveProfiles() {
        return context.getEnvironment().getActiveProfiles();
    }

    public static ApplicationContext getApplicationContext() {
        return context;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBeanByClass(Class<T> tClass) throws BeansException {
        if (context == null) {
            return null;
        }
        return (T) context.getBean(tClass);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBeanByName(String name) throws BeansException {
        if (context == null) {
            return null;
        }
        return (T) context.getBean(name);
    }
}
