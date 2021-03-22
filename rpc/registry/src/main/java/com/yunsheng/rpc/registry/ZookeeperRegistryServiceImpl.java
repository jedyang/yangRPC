package com.yunsheng.rpc.registry;

import com.yunsheng.rpc.common.resistry.RpcServiceUtil;
import com.yunsheng.rpc.common.resistry.ServiceMeta;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * 使用zookeeper实现的注册中心
 *
 * @author yunsheng
 */
@Slf4j
public class ZookeeperRegistryServiceImpl implements RegistryService {

    private static final String registryAddr = "127.0.0.1:2181";
    private static final int BASE_SLEEP_TIME_MS = 1000;
    private static final int MAX_RETRIES = 3;
    private static final String ZK_BASE_PATH = "/yang";

    private final ServiceDiscovery<ServiceMeta> serviceDiscovery;

    public ZookeeperRegistryServiceImpl() throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.newClient(registryAddr, new ExponentialBackoffRetry(BASE_SLEEP_TIME_MS, MAX_RETRIES));
        client.start();

        JsonInstanceSerializer<ServiceMeta> serializer = new JsonInstanceSerializer<>(ServiceMeta.class);
        this.serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceMeta.class)
                .client(client)
                .serializer(serializer)
                .basePath(ZK_BASE_PATH)
                .build();
        this.serviceDiscovery.start();
    }

    @Override
    public void register(ServiceMeta serviceMeta) throws Exception {
        ServiceInstance<ServiceMeta> serviceInstance = ServiceInstance
                .<ServiceMeta>builder()
                .name(RpcServiceUtil.buildServiceKey(serviceMeta))
                .address(serviceMeta.getServiceAddr())
                .port(serviceMeta.getServicePort())
                .payload(serviceMeta)
                .build();
        serviceDiscovery.registerService(serviceInstance);

    }



    @Override
    public void unRegister(ServiceMeta serviceMeta) throws Exception {

    }

    @Override
    public ServiceMeta discovery(ServiceMeta serviceMeta) throws Exception {
        Collection<ServiceInstance<ServiceMeta>> serviceInstances = serviceDiscovery.queryForInstances(RpcServiceUtil.buildServiceKey(serviceMeta));
        ServiceInstance<ServiceMeta> serviceInstance = new RandomLoadBalancer().selectOne((List<ServiceInstance<ServiceMeta>>) serviceInstances, null);
        if (null != serviceInstance) {
            return serviceInstance.getPayload();
        }

        return null;
    }

    @Override
    public void destroy() throws IOException {
        serviceDiscovery.close();
    }
}
