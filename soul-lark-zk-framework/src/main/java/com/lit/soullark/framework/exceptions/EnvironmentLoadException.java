package com.lit.soullark.framework.exceptions;

/**
 * @Author myou
 * @Date 2020/7/1  4:51 下午
 * 配置环境加载异常
 */
public class EnvironmentLoadException extends RuntimeException {
    public EnvironmentLoadException(String msg) {
        super(msg);
    }
}
