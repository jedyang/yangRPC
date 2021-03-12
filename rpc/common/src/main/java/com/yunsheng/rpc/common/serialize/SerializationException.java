package com.yunsheng.rpc.common.serialize;

public class SerializationException extends RuntimeException {

    private static final long serialVersionUID = 3322643737658118861L;

    public SerializationException() {
        super();
    }

    public SerializationException(String msg) {
        super(msg);
    }

    public SerializationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public SerializationException(Throwable cause) {
        super(cause);
    }
}