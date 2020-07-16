package com.lit.soullark.framework.handles;

/**
 * 实现类必须包含一个无参构造函数(暂不支持有参构造实现、若想实现参数传递，可自实现属性构造结构)
 * @param <T>
 */
public interface CompensationStrategyService<T> {
    void compensationDeals(T model);

    class CompensationDemo implements CompensationStrategyService<Object> {
        @Override
        public void compensationDeals(Object model) {
            System.out.println("this is a demo for compensation, you can do something that will get notified when changes occur.");
        }
    }
}
