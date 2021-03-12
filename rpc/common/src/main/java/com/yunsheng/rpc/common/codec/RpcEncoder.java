package com.yunsheng.rpc.common.codec;

import com.yunsheng.rpc.common.protocol.MsgHeader;
import com.yunsheng.rpc.common.protocol.RpcProtocol;
import com.yunsheng.rpc.common.serialize.RpcSerialize;
import com.yunsheng.rpc.common.serialize.SerializeFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 编码器
 * 继承MessageToByteEncoder
 * @author yunsheng
 */
public class RpcEncoder extends MessageToByteEncoder<RpcProtocol<Object>> {
    @Override
    protected void encode(ChannelHandlerContext ctx, RpcProtocol<Object> msg, ByteBuf out) throws Exception {
        /**
         * 编码只要按顺序写即可
         * 注意类型
         * 注意body体的长度需要序列化后才知道
         */
        MsgHeader msgHeader = msg.getMsgHeader();
        out.writeShort(msgHeader.getMagicNum());
        out.writeByte(msgHeader.getVersion());
        out.writeByte(msgHeader.getSerializeType());
        out.writeByte(msgHeader.getMsgType());
        out.writeByte(msgHeader.getStatus());
        out.writeLong(msgHeader.getMsgId());
        RpcSerialize serializer = SerializeFactory.getSerializer(msgHeader.getSerializeType());
        byte[] body = serializer.serialize(msg.getMsgBody());
        // 数据长度
        out.writeInt(body.length);
        out.writeBytes(body);
    }
}
