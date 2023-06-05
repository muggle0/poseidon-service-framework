package com.muggle.psf.genera.ui.psf.gateway.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;

@Configuration
public class SecurityOauthConfig extends AuthorizationServerConfigurerAdapter {

    @Override
    public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory() // 使用in-memory存储
            .withClient("psf-api-gateway") // client_id
            .secret("api-gateway") // client_secret
            .authorizedGrantTypes("authorization_code") // 该client允许的授权类型
            .scopes("app"); // 允许的授权范围
    }

}
