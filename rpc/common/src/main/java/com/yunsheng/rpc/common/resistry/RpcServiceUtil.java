package com.yunsheng.rpc.common.resistry;

import com.yunsheng.rpc.common.resistry.ServiceMeta;

public class RpcServiceUtil {
    public static String buildServiceKey(ServiceMeta serviceMeta) {
        return String.join("#", serviceMeta.getServiceName(), serviceMeta.getServiceVersion());
    }
}
