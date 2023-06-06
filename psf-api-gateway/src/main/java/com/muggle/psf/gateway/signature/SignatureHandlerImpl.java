package com.muggle.psf.gateway.signature;

import com.muggle.psf.common.exception.GatewayException;
import com.muggle.psf.gateway.service.SecretService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * Description
 * Date 2023/6/5
 * Created by muggle
 */
@Component
public class SignatureHandlerImpl implements SignatureHandler {

    @Value("${api.gateway.nonce}")
    private String nonce;

    @Autowired
    private SecretService secretService;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Override
    public Object beforeCheckResult(final SignatureParameter parameter) {
        final String appNonce = parameter.getNonce();
        if (!Objects.equals(nonce, appNonce) || !Objects.isNull(parameter.getAppId())) {
            throw new GatewayException("网关鉴权失败，请校验参数");
        }
        final String secert = secretService.getSecertByAppId(parameter.getAppId(), parameter.getNonce());
        if (Objects.isNull(secert)) {
            throw new GatewayException("网关鉴权失败，请校验凭证");
        }
        return null;
    }

    @Override
    public Object afterCheckResult(final SignatureParameter parameter, final Object brfore) {
        try {
            final long requestTimestamp = Long.parseLong(parameter.getTimestamp());
            final long currentTimestamp = System.currentTimeMillis() / 1000L;
            if (currentTimestamp - requestTimestamp > (long) 1800L) {
                throw new GatewayException("请求超时");
            }
            final String signature = getSignature(parameter);
            if (!Objects.equals(signature, parameter.getRequestSignature())) {
                throw new GatewayException("网关鉴权失败，请校验签名算法");
            }
        } catch (final NoSuchAlgorithmException e) {
            throw new GatewayException(e.getMessage());
        }
        return null;
    }


    public static String getSignature(final SignatureParameter parameter) throws NoSuchAlgorithmException {
        final String decondeStr = parameter.getAppId().concat(parameter.getNonce()).concat(parameter.getTimestamp());
        final MessageDigest md = MessageDigest.getInstance("SHA-256");
        final byte[] d = md.digest(decondeStr.getBytes(StandardCharsets.UTF_8));
        return DatatypeConverter.printHexBinary(d).toUpperCase();
    }
}
