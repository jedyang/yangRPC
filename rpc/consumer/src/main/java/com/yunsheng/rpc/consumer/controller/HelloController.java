package com.yunsheng.rpc.consumer.controller;

import com.yunsheng.rpc.api.HelloService;
import com.yunsheng.rpc.common.anno.YangReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/hello")
public class HelloController {
    @YangReference(version = "2.0.0")
    private HelloService helloService;

    @GetMapping("/hi")
    public String hi() {
        return helloService.sayHello();
    }
}
