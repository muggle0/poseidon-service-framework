package com.muggle.psf.gateway.signature;

import com.muggle.psf.gateway.properties.PsfHeadkeyProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.Optional;

/**
 * Description
 * Date 2023/6/5
 * Created by muggle
 */
public interface SignatureHandler {

    Object beforeCheckResult(SignatureParameter parameter);

    Object afterCheckResult(SignatureParameter parameter, Object brfore);

    default Object checkSign(final ServerHttpRequest request, final PsfHeadkeyProperties properties) {
        final HttpHeaders headers = request.getHeaders();

        final SignatureParameter parameter = buildSignParameter(headers, properties);

        final Object before = this.beforeCheckResult(parameter);
        final Object after = this.afterCheckResult(parameter, before);
        return after;
    }

    public static SignatureParameter buildSignParameter(final HttpHeaders headers, final PsfHeadkeyProperties properties) {
        final SignatureParameter.SignatureParameterBuilder builder = SignatureParameter.builder();
        Optional.ofNullable(headers.get(properties.getAppid())).ifPresent(appids -> {
            for (final String appid : appids) {
                builder.appId(appid);
            }
        });
        Optional.ofNullable(headers.get(properties.getAppsecret())).ifPresent(appsecrets -> {
            for (final String appsecret : appsecrets) {
                builder.appSecret(appsecret);
            }
        });
        Optional.ofNullable(headers.get(properties.getNonce())).ifPresent(nonces -> {
            for (final String nonce : nonces) {
                builder.nonce(nonce);
            }
        });
        Optional.ofNullable(headers.get(properties.getSignature())).ifPresent(signatures -> {
            for (final String signature : signatures) {
                builder.requestSignature(signature);
            }
        });
        Optional.ofNullable(headers.get(properties.getTimestamp())).ifPresent(timestamps -> {
            for (final String timestamp : timestamps) {
                builder.timestamp(timestamp);
            }
        });
        final SignatureParameter parameter = builder.build();
        return parameter;
    }
}
