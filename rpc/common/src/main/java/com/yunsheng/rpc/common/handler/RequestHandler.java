package com.yunsheng.rpc.common.handler;

import com.yunsheng.rpc.common.protocol.*;
import com.yunsheng.rpc.common.resistry.RpcServiceUtil;
import com.yunsheng.rpc.common.resistry.ServiceMeta;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.reflect.FastClass;

import java.util.Map;

/**
 * 请求处理handler
 *
 * @author yunsheng
 */
@Slf4j
public class RequestHandler extends SimpleChannelInboundHandler<RpcProtocol<RequestBody>> {

    /**
     * 本地方法提供者缓存
     */
    private final Map<String, Object> rpcServiceMap;

    public RequestHandler(Map<String, Object> rpcServiceMap) {
        this.rpcServiceMap = rpcServiceMap;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RequestBody> request) throws Exception {
        log.info("===进行业务请求处理===");
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
     *
     * @param msgBody
     * @return
     */
    private Object handle(RequestBody msgBody) throws Exception {
        // 拿到服务bean
        ServiceMeta serviceMeta = new ServiceMeta();
        serviceMeta.setServiceName(msgBody.getClassName());
        serviceMeta.setServiceVersion(msgBody.getServiceVersion());
        String serviceKey = RpcServiceUtil.buildServiceKey(serviceMeta);
        Object serviceBean = rpcServiceMap.get(serviceKey);
        if (null == serviceBean) {
            throw new RuntimeException(String.format("service not exist: %s:%s", msgBody.getClassName(), msgBody.getServiceVersion()));
        }

        Class<?> beanClass = serviceBean.getClass();

        String methodName = msgBody.getMethodName();
        Object[] params = msgBody.getParams();
        Class<?>[] paramTypes = msgBody.getParamTypes();

        // FastClass 机制并没有采用反射的方式调用被代理的方法，而是运行时动态生成一个新的 FastClass 子类，向子类中写入直接调用目标方法的逻辑。
        FastClass fastClass = FastClass.create(beanClass);
        int methodIndex = fastClass.getIndex(methodName, paramTypes);

        return fastClass.invoke(methodIndex, serviceBean, params);
    }
}
