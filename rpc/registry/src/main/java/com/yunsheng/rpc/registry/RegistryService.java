package com.yunsheng.rpc.registry;

import com.yunsheng.rpc.common.resistry.ServiceMeta;

import java.io.IOException;

/**
 * 注册服务接口
 * 统一暴露方法
 * 可选择不同的具体实现
 * @author yunsheng
 */
public interface RegistryService {
    /**
     * 注册服务
     * @param serviceMeta
     * @throws Exception
     */
    void register(ServiceMeta serviceMeta) throws Exception;

    /**
     * 取消注册服务
     * @param serviceMeta
     * @throws Exception
     */
    void unRegister(ServiceMeta serviceMeta) throws Exception;

    /**
     * 服务发现
     * @param serviceMeta
     * @return
     * @throws Exception
     */
    ServiceMeta discovery(ServiceMeta serviceMeta) throws Exception;

    void destroy() throws IOException;
}
