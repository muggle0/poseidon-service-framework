package com.muggle.psf.genera.ui.psf.exception;


/**
 * @program: poseidon-cloud-starter
 * @description:
 * @author: muggle
 * @create: 2019-11-06
 **/

public abstract class BasePoseidonCheckException extends Exception {

    private static final long serialVersionUID = -4597403938823554560L;

    public BasePoseidonCheckException(final String message) {
        super(message);
    }

    public abstract Integer getCode();

}
