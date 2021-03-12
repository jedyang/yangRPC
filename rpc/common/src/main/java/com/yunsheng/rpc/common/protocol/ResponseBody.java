package com.yunsheng.rpc.common.protocol;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResponseBody implements Serializable {
    private static final long serialVersionUID = -4384284864568418105L;
    private Object data;
    private String err;
}
