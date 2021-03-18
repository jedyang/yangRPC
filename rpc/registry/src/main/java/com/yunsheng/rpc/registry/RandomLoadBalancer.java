package com.yunsheng.rpc.registry;

import com.yunsheng.rpc.common.resistry.ServiceMeta;
import org.apache.curator.x.discovery.ServiceInstance;

import java.util.List;
import java.util.Random;

public class RandomLoadBalancer implements ServiceLoadBalancer<ServiceInstance<ServiceMeta>> {
    @Override
    public ServiceInstance<ServiceMeta> selectOne(List<ServiceInstance<ServiceMeta>> servers, String key) {
        int index = new Random().nextInt(servers.size());
        return servers.get(index);
    }
}
