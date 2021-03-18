package com.yunsheng.rpc.provider;

import com.yunsheng.rpc.common.anno.YangService;
import com.yunsheng.rpc.common.resistry.ServiceMeta;
import com.yunsheng.rpc.registry.RegistryService;
import com.yunsheng.rpc.registry.RpcServiceUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * 服务端启动类
 *
 * @author yunsheng
 * @date 2021.3.12
 * <p>
 * 首先在服务提供者启动时，需要启动netty server。
 * 所以实现InitializingBean，在afterPropertiesSet中进行nettyserver启动
 * <p>
 * 第二，所有提供的服务类，在实例化时需要注册到注册中心
 * 所以，需实现BeanPostProcessor，在postProcessAfterInitialization中进行注册
 */
@Slf4j
public class ProviderStarter implements InitializingBean, BeanPostProcessor {

    private String serverAddress;
    @Value("${rpc.server.port}")
    private int serverPort;
    private RegistryService registryService;

    public ProviderStarter(RegistryService registryService) {
        this.registryService = registryService;
    }

    /**
     * 本地服务实例缓存
     */
    private final Map<String, Object> serviceBeanMap = new HashMap<>();

    @Override
    public void afterPropertiesSet() {
        // 必须使用子线程启动，不能阻塞主线程
        new Thread(
                () -> {
                    try {
                        startNettyServer();
                    } catch (Exception e) {
                        log.error("startNettyServer err:", e);
                    }
                }
        ).start();
    }

    private void startNettyServer() throws Exception {
        log.info("====start netty server====");
        this.serverAddress = InetAddress.getLocalHost().getHostAddress();

        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // TODO channelHandler
                        }
                    }).childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture channelFuture = serverBootstrap.bind(this.serverAddress, this.serverPort).sync();
            log.info("rpc server startup success");
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("rpc server startup failed:", e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    /**
     * 进行服务注册
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 有YangService注解的是需要注册的服务
        YangService yangServiceAnno = bean.getClass().getAnnotation(YangService.class);
        if (null != yangServiceAnno) {
            try {
                log.info("serviceName:{}, version: {}", yangServiceAnno.name().getSimpleName(), yangServiceAnno.version());
                // 注册到注册中心
                ServiceMeta serviceMeta = new ServiceMeta();
                serviceMeta.setServiceName(yangServiceAnno.name().getName());
                serviceMeta.setServiceVersion(yangServiceAnno.version());
                serviceMeta.setServiceAddr(this.serverAddress);
                serviceMeta.setServicePort(this.serverPort);
                registryService.register(serviceMeta);

                // 本地缓存bean，处理请求时调用
                serviceBeanMap.put(RpcServiceUtil.buildServiceKey(serviceMeta), bean);
            } catch (Exception e) {
                log.error("register service fail:", e);
            }
        }
        return null;
    }
}
