package com.yunsheng.rpc.registry;

import com.yunsheng.rpc.common.resistry.ServiceMeta;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

//@SpringBootTest
class RegistryApplicationTests {

    @Test
    void register() {
        try {
            RegistryService registryInstance = RegistryFactory.getRegistryInstance(RegistryType.ZOOKEEPER);
            ServiceMeta serviceMeta = new ServiceMeta();
            serviceMeta.setServiceAddr("127.0.0.1");
            serviceMeta.setServicePort(9000);
            serviceMeta.setServiceName("XX");
            serviceMeta.setServiceVersion("2.0.0");
            registryInstance.register(serviceMeta);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    void discovery() {
        try {
            RegistryService registryInstance = RegistryFactory.getRegistryInstance(RegistryType.ZOOKEEPER);
            ServiceMeta serviceMeta = new ServiceMeta();
            serviceMeta.setServiceName("XX");
            serviceMeta.setServiceVersion("2.0.0");
            registryInstance.discovery(serviceMeta);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
