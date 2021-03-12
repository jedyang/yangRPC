package com.yunsheng.rpc.common.handler;

import com.yunsheng.rpc.common.protocol.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 请求处理handler
 * @author yunsheng
 */
public class RequestHandler extends SimpleChannelInboundHandler<RpcProtocol<RequestBody>> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RequestBody> request) throws Exception {
        // 业务处理放到单独的业务线程池
        RequesthandlerPool.submit(() -> {
            RpcProtocol<ResponseBody> response = new RpcProtocol<>();
            // 组装响应头
            MsgHeader msgHeader = request.getMsgHeader();
            // 大部分字段一样，少数修改
            msgHeader.setMsgType((byte) MsgType.RESPONSE.getType());
            // 响应体
            ResponseBody responseBody = new ResponseBody();
            try {
                Object handleResult = handle(request.getMsgBody());
                responseBody.setData(handleResult);
                response.setMsgBody(responseBody);
                msgHeader.setStatus((byte) MsgStatus.SUCCESS.getCode());
                response.setMsgHeader(msgHeader);
            } catch (Exception e) {
                msgHeader.setStatus((byte) MsgStatus.FAIL.getCode());
                responseBody.setErr(e.getMessage());
                e.printStackTrace();
            }

            // 写回响应
            ctx.writeAndFlush(response);
        });
    }

    /**
     * 真正处理业务
     * @param msgBody
     * @return
     */
    private Object handle(RequestBody msgBody) throws Exception {
        // TODO 反射调用方法
        return null;
    }
}
