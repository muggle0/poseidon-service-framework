package com.muggle.psf.gateway.filter;

import com.muggle.psf.gateway.properties.PsfHeadkeyProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

/**
 * Description
 * Date 2023/6/8
 * Created by muggle
 */
public abstract class BaseGatewayFilter implements GlobalFilter {

    private PsfHeadkeyProperties properties;

    public BaseGatewayFilter(final PsfHeadkeyProperties properties) {
        this.properties = properties;
    }

    public abstract void beforeProcess(ServerWebExchange exchange);

    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, final GatewayFilterChain chain) {
        this.beforeProcess(exchange);
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
        return this.afterProcess(exchange, chain);
    }

    protected abstract Mono<Void> afterProcess(ServerWebExchange exchange, GatewayFilterChain chain);
}
