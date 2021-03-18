package com.yunsheng.rpc.registry;

public class RegistryFactory {
    private static RegistryService registryService;

    public static RegistryService getRegistryInstance(RegistryType registryType) throws Exception {
        if (null == registryService) {
            synchronized (RegistryService.class) {
                if (null == registryService) {
                    switch (registryType) {
                        case ZOOKEEPER:
                            registryService = new ZookeeperRegistryServiceImpl();
                            break;
                        default:
                            // TODO
                            break;
                    }
                }
            }
        }

        return registryService;
    }
}
