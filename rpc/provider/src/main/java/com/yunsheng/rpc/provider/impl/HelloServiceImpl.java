package com.yunsheng.rpc.provider.impl;

import com.yunsheng.rpc.api.HelloService;
import com.yunsheng.rpc.common.anno.YangService;

/**
 * 测试service提供者
 * @author yunsheng
 */
@YangService(name = HelloService.class, version = "2.0.0")
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello() {
        return "Hello RPC";
    }
}
