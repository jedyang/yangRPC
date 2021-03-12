package com.yunsheng.rpc.common.serialize;

/**
 * 序列化方法工厂类
 *
 * @author yunsheng
 */
public class SerializeFactory {
    public static RpcSerialize getSerializer(byte type) {
        SerializeTypeEnum typeEnum = SerializeTypeEnum.findByType(type);
        switch (typeEnum) {
            case HESSIAN:
                return new HessianSerialize();
            case JSON:
                // TODO
                return null;
            default:
                throw new IllegalArgumentException("serialize Type is illegal," + type);
        }
    }
}
