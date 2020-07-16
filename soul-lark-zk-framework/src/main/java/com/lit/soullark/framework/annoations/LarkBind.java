package com.lit.soullark.framework.annoations;

import com.lit.soullark.framework.handles.CompensationStrategyService;

import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LarkBind {
    String value() default "";

    boolean isMatchPrefix() default false;

    boolean watched() default false;

    Class<? extends CompensationStrategyService> compensator() default CompensationStrategyService.CompensationDemo.class;
}
