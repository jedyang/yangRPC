package com.yunsheng.rpc.common.protocol;

import lombok.Data;

import java.io.Serializable;

@Data
public class RequestBody implements Serializable {
    private static final long serialVersionUID = 1017984337721374749L;
    private String serviceVersion;
    private String className;
    private String methodName;
    private Class<?>[] paramTypes;
    private Object[] params;

}
