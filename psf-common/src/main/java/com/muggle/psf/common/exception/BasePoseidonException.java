package com.muggle.psf.common.exception;

/**
 * @program: poseidon-cloud-starter
 * @description: 业务异常
 * @author: muggle
 * @create: 2019-11-05
 **/

public abstract class BasePoseidonException extends RuntimeException {


    private static final long serialVersionUID = 7238918244136892808L;

    public BasePoseidonException(final String message) {
        super(message);
    }

    public abstract Integer getCode();

}
