package com.yunsheng.rpc.common.anno;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 服务请求注解
 * @author yunsheng
 */


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
// 每个使用的field都需要加
@Autowired
public @interface YangReference {
    String version() default "1.0.0";
    long timeout() default 5000L;
}



