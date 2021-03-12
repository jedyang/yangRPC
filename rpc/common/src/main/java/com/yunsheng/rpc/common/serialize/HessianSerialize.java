package com.yunsheng.rpc.common.serialize;

import com.caucho.hessian.io.HessianSerializerInput;
import com.caucho.hessian.io.HessianSerializerOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 使用hession序列化
 * @author yunsheng
 */
public class HessianSerialize implements RpcSerialize {
    @Override
    public <T> byte[] serialize(T obj) {
        if (null == obj) {
            throw new NullPointerException();
        }
        byte[] result;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            HessianSerializerOutput hso = new HessianSerializerOutput(bos);
            hso.writeObject(obj);
            hso.flush();
            result = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw new SerializationException(e);
        }
        return result;
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> tClass) {
        if (data == null) {
            throw new NullPointerException();
        }
        T result;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data)) {
            HessianSerializerInput hsi = new HessianSerializerInput(bis);
            result = (T) hsi.readObject(tClass);
        } catch (Exception e) {
            throw new SerializationException(e);
        }

        return result;
    }
}
