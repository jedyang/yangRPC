package com.yunsheng.rpc.common.serialize;

/**
 * 序列化接口
 * 可以有不同的实现类
 * 用户可以选择不同的序列化方法
 */
public interface RpcSerialize {
   <T> byte[] serialize(T obj);
   <T> T deserialize(byte[] data, Class<T> tClass);
}
