package com.yunsheng.rpc.common.serialize;

import lombok.Getter;

public enum SerializeTypeEnum {
    HESSIAN(1),
    JSON(2);

    @Getter
    private final int type;
    SerializeTypeEnum(int type) {
        this.type = type;
    }

    public static SerializeTypeEnum findByType(byte type) {
        for (SerializeTypeEnum serializeTypeEnum : SerializeTypeEnum.values()) {
            if (serializeTypeEnum.type == type) {
                return serializeTypeEnum;
            }
        }
        return HESSIAN;
    }
}
