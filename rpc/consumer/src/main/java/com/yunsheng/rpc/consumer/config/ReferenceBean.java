package com.yunsheng.rpc.consumer.config;

import com.yunsheng.rpc.consumer.proxy.ReferenceProxy;
import com.yunsheng.rpc.registry.RegistryFactory;
import com.yunsheng.rpc.registry.RegistryService;
import com.yunsheng.rpc.registry.RegistryType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

/**
 * 远程调用服务bean的暴露工厂
 * 返回的是经过加工的bean
 */
@Slf4j
public class ReferenceBean implements FactoryBean<Object> {
    /**
     * 被加工的bean
     */
    private Object object;

    /**
     * 大坑
     * 这里定义的所有属性，必须全部设置setter
     * 否则init方法不能执行
     */

    private Class<?> interfaceClass;

    private String serviceVersion;

    private String registryType;
    private String registryAddr;

    private long timeout;

    @Override
    public Object getObject() throws Exception {
        return object;
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }

    /**
     * 被beanFactory调用的自定义方法
     */
    public void init() throws Exception {
        log.info("====ReferenceBean Init====");
        RegistryService registryInstance = RegistryFactory.getRegistryInstance(RegistryType.valueOf(registryType));

        this.object = Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new ReferenceProxy(registryInstance, serviceVersion, timeout));
    }

    public void setInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public void setRegistryType(String registryType) {
        this.registryType = registryType;
    }

    public void setRegistryAddr(String registryAddr) {
        this.registryAddr = registryAddr;
    }
}
