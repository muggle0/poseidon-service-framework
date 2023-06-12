package com.muggle.psf.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.muggle.psf.gateway.properties.PsfHeadkeyProperties;
import com.muggle.psf.gateway.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;

/**
 * Description
 * Date 2023/6/7
 * Created by muggle
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "gateway", name = "api.auth.enabled", havingValue = "true", matchIfMissing = true)
@Order(2)
public class AuthFilter extends BaseGatewayFilter {


    private PsfHeadkeyProperties properties;

    private AuthService authService;

    @Autowired
    public AuthFilter(final PsfHeadkeyProperties properties, final AuthService authService) {
        super(properties);
        this.properties = properties;
        this.authService = authService;
    }


    @Override
    protected Mono<Void> afterProcess(final ServerWebExchange exchange, final GatewayFilterChain chain) {
        final boolean isAuth = authService.isAuth(exchange);
        if (isAuth) {
            return chain.filter(exchange);
        }
        final ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().add("Content-Type", "application/json;charset=utf-8");
        final byte[] bytes = JSON.toJSONString("用户未登录").getBytes(Charset.forName("utf-8"));
        final DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }
}
