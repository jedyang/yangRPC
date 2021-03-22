package com.yunsheng.rpc.common.request;

import com.yunsheng.rpc.common.protocol.ResponseBody;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class RpcRequestHolder {
    /**
     * 应该用分布式id生成
     */
    public final static AtomicLong MSG_ID_GEN = new AtomicLong(0);

    public final static Map<Long, RpcFuture<ResponseBody>> REQUEST_MAP = new ConcurrentHashMap<>();
}
