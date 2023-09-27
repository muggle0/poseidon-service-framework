package com.muggle.psf.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.muggle.psf.common.exception.GatewayException;
import com.muggle.psf.common.result.ResultBean;
import com.muggle.psf.gateway.properties.PsfHeadkeyProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Description
 * Date 2023/6/8
 * Created by muggle
 */
@Slf4j
public abstract class BaseGatewayFilter implements GlobalFilter {

    private PsfHeadkeyProperties properties;

    public BaseGatewayFilter(final PsfHeadkeyProperties properties) {
        this.properties = properties;
    }

    /**
     * 自定义前置
     * @param exchange
     */
    public abstract void beforeProcess(ServerWebExchange exchange);

    /**
     * 对全局配置的路由放行
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, final GatewayFilterChain chain) {
        try {
            this.beforeProcess(exchange);
        } catch (final Exception e) {
            log.error("BaseGatewayFilter beforeProcess error", e);
            final ResultBean<Object> error = com.muggle.psf.common.result.ResultBean.error(e.getMessage());
            if (e instanceof GatewayException) {
                error.setCode(((GatewayException) e).getCode());
            }
            final ServerHttpResponse response = exchange.getResponse();
            response.getHeaders().add("Content-Type", "application/json;charset=utf-8");
            final byte[] bytes = JSON.toJSONString(error).getBytes(Charset.forName("utf-8"));
            final DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        }
        final AntPathMatcher antPathMatcher = new AntPathMatcher();
        final ServerHttpRequest request = exchange.getRequest();
        final URI uri = request.getURI();
        final String path = uri.getPath();
        if (!StringUtils.isEmpty(properties.getOpenurl())) {
            final boolean match = antPathMatcher.match(properties.getOpenurl(), path);
            if (match) {
                return chain.filter(exchange);
            }
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

    /**
     * 自定义后置
     * @param exchange
     * @param chain
     * @return
     */
    protected abstract Mono<Void> afterProcess(ServerWebExchange exchange, GatewayFilterChain chain);
}
