package com.muggle.psf.gateway.signature;

import com.muggle.psf.gateway.properties.PsfHeadkeyProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;

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
        final SignatureParameter.SignatureParameterBuilder builder = SignatureParameter.builder();
        for (final String appid : headers.get(properties.getAppid())) {
            builder.appId(appid);
        }
        for (final String appsecret : headers.get(properties.getAppsecret())) {
            builder.appSecret(appsecret);
        }
        for (final String nonce : headers.get(properties.getNonce())) {
            builder.nonce(nonce);
        }
        for (final String signature : headers.get(properties.getSignature())) {
            builder.requestSignature(signature);
        }
        for (final String timestamp : headers.get(properties.getTimestamp())) {
            builder.timestamp(timestamp);
        }
        final SignatureParameter parameter = builder.build();
        final Object before = this.beforeCheckResult(parameter);
        final Object after = this.afterCheckResult(parameter, before);
        return after;
    }
}
