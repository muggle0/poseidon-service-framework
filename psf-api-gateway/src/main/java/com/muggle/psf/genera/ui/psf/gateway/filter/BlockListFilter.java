package com.muggle.psf.genera.ui.psf.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
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
public class BlockListFilter implements GlobalFilter {

    @Value("${gateway.api.openurl}")
    private String openurl;

    @Value("${gateway.api.excludeurls}")
    private List<String> excludeurls;


    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, final GatewayFilterChain chain) {
        final AntPathMatcher antPathMatcher = new AntPathMatcher();
        final ServerHttpRequest request = exchange.getRequest();
        final URI uri = request.getURI();
        final String path = uri.getPath();
        final boolean match = antPathMatcher.match(openurl, path);
        if (match) {
            return chain.filter(exchange);
        }
        if (!CollectionUtils.isEmpty(excludeurls)) {
            for (final String exclude : excludeurls) {
                if (antPathMatcher.match(exclude, path)) {
                    return chain.filter(exchange);
                }
            }
        }
        final HttpHeaders headers = request.getHeaders();
        headers.get()
    }

}
