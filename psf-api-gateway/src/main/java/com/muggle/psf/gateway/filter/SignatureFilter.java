package com.muggle.psf.gateway.filter;

import com.muggle.psf.common.exception.GatewayException;
import com.muggle.psf.gateway.properties.PsfHeadkeyProperties;
import com.muggle.psf.gateway.signature.SignatureHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

/**
 * Description
 * Date 2023/6/5
 * Created by muggle
 *
 * @author muggle
 */
@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class SignatureFilter implements GlobalFilter {

    private final PsfHeadkeyProperties properties;

    private final SignatureHandler signatureHandler;

    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, final GatewayFilterChain chain) {
        final AntPathMatcher antPathMatcher = new AntPathMatcher();
        final ServerHttpRequest request = exchange.getRequest();
        final URI uri = request.getURI();
        final String path = uri.getPath();
        final boolean match = antPathMatcher.match(properties.getOpenurl(), path);
        if (match) {
            return chain.filter(exchange);
        }
        final List<String> excludeurls = properties.getExcludeurls();
        if (!CollectionUtils.isEmpty(excludeurls)) {
            for (final String exclude : excludeurls) {
                if (antPathMatcher.match(exclude, path)) {
                    return chain.filter(exchange);
                }
            }
        }
        try {
            signatureHandler.checkSign(request, properties);
        } catch (final GatewayException e) {
            return Mono.error(() -> e);
        }
        return chain.filter(exchange);
    }

}
