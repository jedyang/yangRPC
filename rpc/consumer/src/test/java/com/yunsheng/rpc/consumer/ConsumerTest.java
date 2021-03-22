package com.yunsheng.rpc.consumer;

import com.yunsheng.rpc.common.codec.RpcDecoder;
import com.yunsheng.rpc.common.codec.RpcEncoder;
import com.yunsheng.rpc.common.handler.ResponseHandler;
import com.yunsheng.rpc.common.protocol.*;
import com.yunsheng.rpc.common.request.RpcRequestHolder;
import com.yunsheng.rpc.common.serialize.SerializeTypeEnum;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsumerTest {
    public static void main(String[] args) {
        new ConsumerTest().start();
    }

    public void start() {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();

        try {
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new RpcEncoder())
                                    .addLast(new RpcDecoder())
                                    .addLast(new ResponseHandler());

                        }
                    });

            ChannelFuture channelFuture = bootstrap.connect("10.190.24.54", 12580).sync();
            log.info("===echoClient start===");
            // 发送请求
            RpcProtocol<RequestBody> requestProtocol = buildRequest();
            channelFuture.channel().writeAndFlush(requestProtocol);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }

    }

    private RpcProtocol<RequestBody> buildRequest() {
        RpcProtocol<RequestBody> requestProtocol = new RpcProtocol<>();
        // 组装协议头
        MsgHeader msgHeader = new MsgHeader();
        msgHeader.setMagicNum(ProtocolConstants.MAGIC);
        msgHeader.setVersion(ProtocolConstants.VERSION);
        long msgId = RpcRequestHolder.MSG_ID_GEN.incrementAndGet();
        msgHeader.setMsgId(msgId);
        msgHeader.setSerializeType((byte) SerializeTypeEnum.HESSIAN.getType());
        msgHeader.setMsgType((byte) MsgType.REQUEST.getType());
        msgHeader.setStatus((byte) 0x1);
        requestProtocol.setMsgHeader(msgHeader);

        // 组装请求体
        RequestBody requestBody = new RequestBody();
        requestBody.setServiceVersion("2.0.0");
        requestBody.setClassName("com.yunsheng.rpc.api.HelloService");
        requestBody.setMethodName("sayHello");
        requestBody.setParamTypes(null);
        requestBody.setParams(null);
        requestProtocol.setMsgBody(requestBody);

        return requestProtocol;
    }
}
