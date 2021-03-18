package com.yunsheng.rpc.common.anno;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 服务提供者注解
 * @author yunsheng
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
// 省的每个service都要添加
@Component
public @interface YangService {
    Class<?> name() default Object.class;
    String version() default "1.0.0";
}
