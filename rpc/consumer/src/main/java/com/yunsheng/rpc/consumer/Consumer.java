package com.yunsheng.rpc.consumer;

import com.yunsheng.rpc.common.codec.RpcDecoder;
import com.yunsheng.rpc.common.codec.RpcEncoder;
import com.yunsheng.rpc.common.handler.ResponseHandler;
import com.yunsheng.rpc.common.protocol.RequestBody;
import com.yunsheng.rpc.common.protocol.RpcProtocol;
import com.yunsheng.rpc.common.resistry.ServiceMeta;
import com.yunsheng.rpc.registry.RegistryService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Consumer {
    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;

    public Consumer() {
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup(4);
        bootstrap.group(eventLoopGroup)
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
    }

    public void sendRequest(RpcProtocol<RequestBody> requestProtocol, RegistryService registryInstance) {
        log.info("===sendRequest===");
        RequestBody msgBody = requestProtocol.getMsgBody();
        ServiceMeta serviceMeta = new ServiceMeta();
        serviceMeta.setServiceName(msgBody.getClassName());
        serviceMeta.setServiceVersion(msgBody.getServiceVersion());
        try {
            // 得到服务的提供地址
            ServiceMeta discovery = registryInstance.discovery(serviceMeta);
            if (null != discovery) {
                // 建立链接
                ChannelFuture channelFuture = bootstrap.connect(discovery.getServiceAddr(), discovery.getServicePort()).sync();
                channelFuture.addListener(future -> {
                    log.info(future.toString());
                    if (channelFuture.isSuccess()) {
                        log.info("connect rpc server {} on port {} success", discovery.getServiceAddr(), discovery.getServicePort());
                    } else {
                        log.error("connect rpc server {} on port {} failed", discovery.getServiceAddr(), discovery.getServicePort());
                        channelFuture.cause().printStackTrace();
                        eventLoopGroup.shutdownGracefully();
                    }
                });
                // 发送请求
                channelFuture.channel().writeAndFlush(requestProtocol);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
