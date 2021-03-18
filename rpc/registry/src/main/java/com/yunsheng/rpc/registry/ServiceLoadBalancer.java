package com.yunsheng.rpc.registry;

import java.util.List;

/**
 * 负载均衡器
 * 可有多个实现
 */
public interface ServiceLoadBalancer<T> {
    T selectOne(List<T> servers, String key);
}
