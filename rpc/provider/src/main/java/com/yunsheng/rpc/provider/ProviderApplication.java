package com.yunsheng.rpc.provider;

import com.yunsheng.rpc.registry.RegistryFactory;
import com.yunsheng.rpc.registry.RegistryService;
import com.yunsheng.rpc.registry.RegistryType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ProviderApplication {
    @Value("${rpc.server.registryType}")
    private String registryTypeValue;

    public static void main(String[] args) {
        SpringApplication.run(ProviderApplication.class, args);
    }

    @Bean
    public ProviderStarter providerInit() throws Exception {
        RegistryType registryType = RegistryType.valueOf(registryTypeValue);
        RegistryService registryService = RegistryFactory.getRegistryInstance(registryType);
        ProviderStarter providerStarter = new ProviderStarter(registryService);
        return providerStarter;
    }

}
