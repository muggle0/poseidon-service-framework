package com.muggle.psf.gateway.service.impl;

import com.muggle.psf.gateway.properties.PsfHeadkeyProperties;
import com.muggle.psf.gateway.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;

/**
 * Description
 * Date 2023/6/8
 * Created by muggle
 */
@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@ConditionalOnMissingBean
public class DefaultAuthService implements AuthService {

    @Value("${gateway.api.header.token}")
    private String tokenHead;

    private final PsfHeadkeyProperties properties;


    @Override
    public boolean isAuth(final ServerWebExchange exchange) {
        final String token = this.getToken(exchange);
        if (StringUtils.isEmpty(token)) {
            return false;
        }
        // todo
        return true;
    }

    private String getToken(final ServerWebExchange exchange) {
        final ServerHttpRequest request = exchange.getRequest();
        final List<String> tokenHeaders = request.getHeaders().get(tokenHead);
        String token = null;
        for (final String tokenHeader : tokenHeaders) {
            token = tokenHeader;
        }
        return token;
    }

    @Override
    public String getUsercode(final ServerWebExchange exchange) {
        return null;
    }
}
