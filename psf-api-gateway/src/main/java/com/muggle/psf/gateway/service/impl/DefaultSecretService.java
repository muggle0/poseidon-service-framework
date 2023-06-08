package com.muggle.psf.gateway.service.impl;

import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.muggle.psf.gateway.service.SecretService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Description
 * Date 2023/6/6
 * Created by muggle
 */

@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@ConditionalOnMissingBean
public class DefaultSecretService implements SecretService {

    private final JdbcTemplate jdbcTemplate;

    @Cached(name = "secretCache", cacheType = CacheType.REMOTE)
    @Override
    public String getSecertByAppId(final String appId, final String nonce) {
        final String sql = "SELECT s.`app_secret` FROM psf_secret s WHERE s.`app_id`='%s' AND s.`nonce`='%s'";
        try {
            final String secret = jdbcTemplate.queryForObject(String.format(sql, appId, nonce), String.class);
            return secret;
        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
    }

}
