package com.muggle.psf.gateway.config;

import com.muggle.psf.gateway.properties.PsfHeadkeyProperties;
import com.muggle.psf.gateway.service.AuthService;
import com.muggle.psf.gateway.service.BlackListService;
import com.muggle.psf.gateway.service.SecretService;
import com.muggle.psf.gateway.service.impl.DefaultAuthService;
import com.muggle.psf.gateway.service.impl.DefaultBlackListService;
import com.muggle.psf.gateway.service.impl.DefaultSecretService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Description
 * Date 2023/6/12
 * Created by muggle
 */
@Configuration
public class CommonConfig {
    @Value("${gateway.api.header.token}")
    private String tokenHead;

    @Autowired
    private PsfHeadkeyProperties properties;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Bean
    @ConditionalOnMissingBean
    public AuthService authService() {
        return new DefaultAuthService(tokenHead, properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public BlackListService blackListService() {
        return new DefaultBlackListService();
    }

    @Bean
    @ConditionalOnMissingBean
    public SecretService secretService() {
        return new DefaultSecretService(jdbcTemplate);
    }
}
