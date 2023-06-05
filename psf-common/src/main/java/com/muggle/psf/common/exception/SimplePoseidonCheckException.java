package com.muggle.psf.common.exception;

/**
 * @program: poseidon-cloud-starter
 * @description:
 * @author: muggle
 * @create: 2019-11-06
 **/

public class SimplePoseidonCheckException extends BasePoseidonCheckException {
    private static final long serialVersionUID = 1603693807470257170L;
    private Integer code;

    public SimplePoseidonCheckException(final String message) {
        super(message);
        this.code = 5001;
    }

    public SimplePoseidonCheckException(final String message, final Integer code) {
        super(message);
        this.code = code;
    }

    @Override
    public Integer getCode() {
        return code;
    }
}
