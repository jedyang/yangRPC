package com.yunsheng.rpc.common.handler;

import com.yunsheng.rpc.common.protocol.ResponseBody;
import com.yunsheng.rpc.common.protocol.RpcProtocol;
import com.yunsheng.rpc.common.request.RpcFuture;
import com.yunsheng.rpc.common.request.RpcRequestHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 客户端处理服务端响应的handler
 *
 * @author yunsheng
 */
@Slf4j
public class ResponseHandler extends SimpleChannelInboundHandler<RpcProtocol<ResponseBody>> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<ResponseBody> msg) throws Exception {
        log.info("客户端处理服务端响应的handler");
        /**
         * 前面经过RpcDecoder解码，已经解析出响应
         * 客户端发送请求时，本地存储了msgid和future的映射
         * 这里要做的事是，根据msgId，查出是哪个请求的响应
         * 给响应赋值
         */
        long msgId = msg.getMsgHeader().getMsgId();
        RpcFuture<ResponseBody> rpcFuture = RpcRequestHolder.REQUEST_MAP.get(msgId);
        ResponseBody msgBody = msg.getMsgBody();
        log.info(msgBody.toString());
        rpcFuture.getPromise().setSuccess(msgBody);
    }
}
