package com.yunsheng.rpc.registry;

import com.yunsheng.rpc.common.resistry.ServiceMeta;

public class RpcServiceUtil {
    public static String buildServiceKey(ServiceMeta serviceMeta) {
        return String.join("#", serviceMeta.getServiceName(), serviceMeta.getServiceVersion());
    }
}
