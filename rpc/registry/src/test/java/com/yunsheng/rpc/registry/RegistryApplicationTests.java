package com.yunsheng.rpc.registry;

import com.yunsheng.rpc.common.resistry.ServiceMeta;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@Slf4j
public class RegistryApplicationTests {

    private RegistryService registryInstance;

    @Before
    public void init() {
        try {
            registryInstance = RegistryFactory.getRegistryInstance(RegistryType.ZOOKEEPER);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @After
    public void close() throws Exception {
        registryInstance.destroy();
    }

    @Test
    public void register() {
        try {
            ServiceMeta serviceMeta1 = new ServiceMeta();
            serviceMeta1.setServiceAddr("127.0.0.1");
            serviceMeta1.setServicePort(9001);
            serviceMeta1.setServiceName("XX");
            serviceMeta1.setServiceVersion("2.0.0");
            registryInstance.register(serviceMeta1);

            ServiceMeta serviceMeta2 = new ServiceMeta();
            serviceMeta2.setServiceAddr("127.0.0.2");
            serviceMeta2.setServicePort(9002);
            serviceMeta2.setServiceName("XX");
            serviceMeta2.setServiceVersion("2.0.0");
            registryInstance.register(serviceMeta2);

            ServiceMeta serviceMeta3 = new ServiceMeta();
            serviceMeta3.setServiceAddr("127.0.0.3");
            serviceMeta3.setServicePort(9003);
            serviceMeta3.setServiceName("XX");
            serviceMeta3.setServiceVersion("2.0.0");
            registryInstance.register(serviceMeta3);

            Thread.sleep(1000*60);
            ServiceMeta serviceMeta = new ServiceMeta();
            serviceMeta.setServiceName("XX");
            serviceMeta.setServiceVersion("2.0.0");
            for (int i = 0; i < 10; i++) {
                ServiceMeta discovery = registryInstance.discovery(serviceMeta);
                log.info(discovery.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void discovery() {
        try {
            ServiceMeta serviceMeta = new ServiceMeta();
            serviceMeta.setServiceName("XX");
            serviceMeta.setServiceVersion("2.0.0");
            for (int i = 0; i < 10; i++) {
                ServiceMeta discovery = registryInstance.discovery(serviceMeta);
                log.info(discovery.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
