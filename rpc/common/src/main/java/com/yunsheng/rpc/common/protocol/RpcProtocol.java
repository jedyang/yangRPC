package com.yunsheng.rpc.common.protocol;

import lombok.Data;

import java.io.Serializable;

/**
 * 自定义传输协议
 * @author yunsheng
 *
 * +---------------------------------------------------------------+
 *
 * | 魔数 2byte | 协议版本号 1byte | 序列化算法 1byte | 报文类型 1byte  |
 *
 * +---------------------------------------------------------------+
 *
 * | 状态 1byte |        消息 ID 8byte     |      数据长度 4byte     |
 *
 * +---------------------------------------------------------------+
 *
 * |                   数据内容 （长度不定）                          |
 *
 * +---------------------------------------------------------------+
 */
@Data
public class RpcProtocol<T> implements Serializable {
    private static final long serialVersionUID = -3875822883129951493L;
    // 消息头格式是统一的
    private MsgHeader msgHeader;
    // 消息体分为请求和响应
    private T msgBody;
}
