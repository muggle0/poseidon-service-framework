package com.muggle.psf.common.exception;

/**
 * Description
 * Date 2023/6/5
 * Created by muggle
 */
public class GatewayException extends BasePoseidonException {

    private static final long serialVersionUID = -5832568357679058906L;
    private Integer code = 50001;

    public GatewayException(final String message) {
        super(message);
        this.code = 5001;
    }

    public GatewayException(final String message, final Integer code) {
        super(message);
        this.code = code;
    }

    @Override
    public Integer getCode() {
        return code;
    }
}
