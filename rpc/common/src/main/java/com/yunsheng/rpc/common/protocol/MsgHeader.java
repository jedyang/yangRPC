package com.yunsheng.rpc.common.protocol;

import lombok.Data;

import java.io.Serializable;

/**
 *  * +---------------------------------------------------------------+
 *  *
 *  * | 魔数 2byte | 协议版本号 1byte | 序列化算法 1byte | 报文类型 1byte  |
 *  *
 *  * +---------------------------------------------------------------+
 *  *
 *  * | 状态 1byte |        消息 ID 8byte     |      数据长度 4byte     |
 *  *
 *  * +---------------------------------------------------------------+
 */
@Data
public class MsgHeader implements Serializable {
    private static final long serialVersionUID = -1928421629950984879L;
    private short magicNum;
    private byte version;
    private byte serializeType;
    private byte msgType;
    private byte status;
    private long msgId;
    private int dataSize;
}
