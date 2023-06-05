package com.muggle.psf.genera.ui.psf.exception;

/**
 * @program: poseidon-cloud-starter
 * @description: 通用业务异常
 * @author: muggle
 * @create: 2019-11-05
 **/

public class SimplePoseidonException extends BasePoseidonException {

    private static final long serialVersionUID = 3936906943255876260L;
    private Integer code;

    public SimplePoseidonException(final String message, final Integer code) {
        super(message);
        this.code = code;
    }

    public SimplePoseidonException(final String message) {
        super(message);
        this.code = 5001;
    }

    @Override
    public Integer getCode() {
        return code;
    }
}
