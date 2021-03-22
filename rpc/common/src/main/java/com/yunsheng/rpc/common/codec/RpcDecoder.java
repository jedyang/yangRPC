package com.yunsheng.rpc.common.codec;

import com.yunsheng.rpc.common.protocol.*;
import com.yunsheng.rpc.common.serialize.RpcSerialize;
import com.yunsheng.rpc.common.serialize.SerializeFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 解码器
 *
 * @author yunsheng
 */
@Slf4j
public class RpcDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        log.info("===RpcDecoder Handler===");

        // 数据首先必须大于头信息的长度
        if (in.readableBytes() < ProtocolConstants.HEADER_TOTAL_LEN) {
            return;
        }
        // 可能头信息全了，消息体还不全。先保存下读位点。如果后面判断body体还不全，重置回这个位点重新读。
        in.markReaderIndex();

        // 判断魔数
        short magicNum = in.readShort();
        if (magicNum != ProtocolConstants.MAGIC) {
            throw new IllegalArgumentException("magicNum is illegal, " + magicNum);
        }
        byte version = in.readByte();
        byte serializeType = in.readByte();
        RpcSerialize rpcSerialize = SerializeFactory.getSerializer(serializeType);
        if (null == rpcSerialize) {
            throw new IllegalArgumentException("serializeType is illegal, " + serializeType);
        }
        byte msgType = in.readByte();
        MsgType msgTypeEnum = MsgType.findByType(msgType);
        if (null == msgTypeEnum) {
            throw new IllegalArgumentException("MsgType is illegal, " + msgType);
        }

        byte status = in.readByte();
        long msgId = in.readLong();

        int dataSize = in.readInt();
        if (in.readableBytes() < dataSize) {
            // 剩下的数据如果比记录的消息体长度小，不全，重读。
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataSize];
        in.readBytes(data);

        // 组装MsgHeader
        MsgHeader header = new MsgHeader();
        header.setMagicNum(magicNum);
        header.setVersion(version);
        header.setSerializeType(serializeType);
        header.setStatus(status);
        header.setMsgId(msgId);
        header.setMsgType(msgType);
        header.setDataSize(dataSize);

        // 处理消息体。根据不同的类型。
        switch (msgTypeEnum) {
            case REQUEST:
                RequestBody requestBody = rpcSerialize.deserialize(data, RequestBody.class);
                RpcProtocol<RequestBody> requestMsg = new RpcProtocol<>();
                requestMsg.setMsgHeader(header);
                requestMsg.setMsgBody(requestBody);
                out.add(requestMsg);
                break;
            case RESPONSE:
                ResponseBody responseBody = rpcSerialize.deserialize(data, ResponseBody.class);
                RpcProtocol<ResponseBody> responseMsg = new RpcProtocol<>();
                responseMsg.setMsgHeader(header);
                responseMsg.setMsgBody(responseBody);
                out.add(responseMsg);
                break;
            case HEARTBEAT:
                break;
            default:
                break;
        }
    }
}
