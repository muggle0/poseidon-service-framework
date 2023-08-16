package com.muggle.psf.gateway.filter;

import com.muggle.psf.gateway.properties.PsfHeadkeyProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Description
 * Date 2023/6/12
 * Created by muggle
 */

public class LogFilter extends BaseGatewayFilter {
    public LogFilter(final PsfHeadkeyProperties properties) {
        super(properties);
    }

    @Override
    public void beforeProcess(final ServerWebExchange exchange) {

    }

    @Override
    protected Mono<Void> afterProcess(final ServerWebExchange exchange, final GatewayFilterChain chain) {
        return null;
    }
}
