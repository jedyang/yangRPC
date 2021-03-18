package com.yunsheng.rpc.consumer.proxy;

import com.yunsheng.rpc.common.protocol.*;
import com.yunsheng.rpc.common.request.RpcRequestHolder;
import com.yunsheng.rpc.common.serialize.SerializeTypeEnum;
import com.yunsheng.rpc.registry.RegistryService;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ReferenceProxy implements InvocationHandler {

    private final RegistryService registryInstance;
    private final String serviceVersion;
    private final long timeout;

    public ReferenceProxy(RegistryService registryInstance, String serviceVersion, long timeout) {
        this.serviceVersion = serviceVersion;
        this.timeout = timeout;
        this.registryInstance = registryInstance;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 方法调用转为远程服务调用
        RpcProtocol<RequestBody> requestProtocol = new RpcProtocol<>();
        // 组装协议头
        MsgHeader msgHeader = new MsgHeader();
        msgHeader.setMagicNum(ProtocolConstants.MAGIC);
        msgHeader.setVersion(ProtocolConstants.VERSION);
        long msgId = RpcRequestHolder.MSG_ID_GEN.incrementAndGet();
        msgHeader.setMsgId(msgId);
        msgHeader.setSerializeType((byte) SerializeTypeEnum.HESSIAN.getType());
        msgHeader.setMsgType((byte) MsgType.REQUEST.getType());
        msgHeader.setStatus((byte) 0x1);
        requestProtocol.setMsgHeader(msgHeader);

        // 组装请求体
        RequestBody requestBody = new RequestBody();
        requestBody.setServiceVersion(this.serviceVersion);
        requestBody.setClassName(method.getDeclaringClass().getName());
        requestBody.setMethodName(method.getName());
        requestBody.setParamTypes(method.getParameterTypes());
        requestBody.setParams(args);
        requestProtocol.setMsgBody(requestBody);



        return null;
    }
}
