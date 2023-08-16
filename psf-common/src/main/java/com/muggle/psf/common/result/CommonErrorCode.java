package com.muggle.psf.common.result;

/**
 * Description
 * Date 2023/6/13
 * Created by muggle
 */
public enum CommonErrorCode {
    GATEWAY(50001, "网关异常"),
    LIMIT(50002, "服务限流");

    CommonErrorCode(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    public int code;

    public String message;
}
