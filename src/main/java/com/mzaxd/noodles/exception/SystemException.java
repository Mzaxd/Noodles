package com.mzaxd.noodles.exception;

import com.mzaxd.noodles.enums.AppHttpCodeEnum;

/**
 *
 * @author mzaxd
 * @date 11/27/22 3:19 AM
 */

public class SystemException extends RuntimeException{

    private final int code;

    private final String msg;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public SystemException(AppHttpCodeEnum httpCodeEnum) {
        super(httpCodeEnum.getMsg());
        this.code = httpCodeEnum.getCode();
        this.msg = httpCodeEnum.getMsg();
    }
    
}
