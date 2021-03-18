package com.yunsheng.rpc.common.request;

import java.util.concurrent.atomic.AtomicLong;

public class RpcRequestHolder {
    /**
     * 应该用分布式id生成
     */
    public final static AtomicLong MSG_ID_GEN = new AtomicLong(0);
}
