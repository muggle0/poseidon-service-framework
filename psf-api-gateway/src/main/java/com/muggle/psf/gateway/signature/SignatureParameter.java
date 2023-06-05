package com.muggle.psf.gateway.signature;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

/**
 * Description
 * Date 2023/6/5
 * Created by muggle
 */
@Builder
@Getter
public class SignatureParameter implements Serializable {
    private static final long serialVersionUID = 8365025020560088593L;
    private String requestIp;
    private String timestamp;
    private String nonce;
    private String requestSignature;
    private String appId;
    private String appSecret;
}
