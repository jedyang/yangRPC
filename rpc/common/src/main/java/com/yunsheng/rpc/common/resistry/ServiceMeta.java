package com.yunsheng.rpc.common.resistry;

import lombok.Data;

/**
 * 服务注册
 * 服务元信息
 *
 * @author yunsheng
 */
@Data
public class ServiceMeta {

    private String serviceName;

    private String serviceVersion;

    private String serviceAddr;

    private int servicePort;

}