package com.lit.soullark.framework.handles.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lit.soullark.framework.exceptions.InvocationHandlerException;
import com.lit.soullark.framework.exceptions.PathNoDataException;
import com.lit.soullark.framework.exceptions.ProxyException;
import com.lit.soullark.framework.handles.CompensationStrategyService;
import com.lit.soullark.framework.utils.FrameworkThreadPool;
import com.lit.soullark.framework.utils.SpringContextUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.zookeeper.data.Stat;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;

/**
 * @Author myou
 * @Date 2020/7/3  10:12 上午
 */
public class LarkInvocationHandler {
    private CuratorFramework client;
    private String path;

    public LarkInvocationHandler(CuratorFramework client, String path) {
        assert client != null : "CuratorFramework target can not be null";
        this.client = client;
        this.path = path;
    }


    public <T> T bindNodeData(Class<T> model, boolean watched, Class<? extends CompensationStrategyService> compensator) throws Exception {
        Stat stat = this.client.checkExists().forPath(path);
        // getDataLength节点没有数据是否为0待测试
        if (stat.getDataLength() < 0)
            throw new PathNoDataException(String.format("%s path get data failed, please checkExists", path));
        byte[] bytes = this.client.getData().forPath(path);
        NodeCache nodeCache = new NodeCache(this.client, this.path);
        nodeCache.start(true);
        if (watched) {
            nodeCache.getListenable().addListener(() -> {
                byte[] data = nodeCache.getCurrentData().getData();
                T byClass = SpringContextUtil.getBeanByClass(model);
                HashMap<String, String> map = new ObjectMapper().readValue(data, new TypeReference<HashMap<String, String>>() {
                });
                assert byClass != null : String.format("%s not found", model.getSimpleName());
                Field[] fields = byClass.getClass().getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    field.set(byClass, map.get(field.getName()));
                }
                // 补偿通知
                ExecutorService executorService = FrameworkThreadPool.getExecutorService();
                executorService.execute(() -> {
                    Method method = null;
                    try {
                        method = compensator.getMethod("compensationDeals", byClass.getClass());
                        method.invoke(compensator.newInstance(), byClass);
                    } catch (NoSuchMethodException | IllegalAccessException e) {
                        throw new ProxyException(String.format("can not found method or IllegalAccess,class:%s", byClass.getClass().getName()));
                    } catch (InstantiationException | InvocationTargetException e) {
                        throw new InvocationHandlerException(String.format("reflect newInstance or InvocationTarget error,class:%s", byClass.getClass().getName()));
                    }
                });
            });
        }
        return new ObjectMapper().readValue(bytes, model);
    }
}
