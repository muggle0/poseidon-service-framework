package com.muggle.psf.common.exception;


/**
 * @program: poseidon-cloud-starter
 * @description:
 * @author: muggle
 * @create: 2019-11-06
 **/

public abstract class BasePoseidonCheckException extends Exception {

    private static final long serialVersionUID = -9192168220088676200L;

    public BasePoseidonCheckException(final String message) {
        super(message);
    }

    public abstract Integer getCode();

}
