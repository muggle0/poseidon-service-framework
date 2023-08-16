package com.muggle.psf.gateway.service;

/**
 * Description
 * Date 2023/6/6
 * Created by muggle
 */
public interface SecretService {

    String getSecertByAppId(String appId, String nonce);
}
