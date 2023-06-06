package com.muggle.psf.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.muggle.psf.common.exception.GatewayException;
import com.muggle.psf.common.result.ResultBean;
import com.muggle.psf.gateway.properties.PsfHeadkeyProperties;
import com.muggle.psf.gateway.signature.SignatureHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.Charset;
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
@ConditionalOnProperty(prefix = "gateway", name = "api.signature.enabled", havingValue = "redis", matchIfMissing = true)
@Order(1)
public class SignatureFilter implements GlobalFilter {

    private final PsfHeadkeyProperties properties;

    private final SignatureHandler signatureHandler;

    @Autowired
    public SignatureFilter(final PsfHeadkeyProperties properties, final SignatureHandler signatureHandler) {
        this.properties = properties;
        this.signatureHandler = signatureHandler;
        log.info("SignatureFilter init 》》》》》");
    }

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
            final ResultBean<Object> error = ResultBean.error(e.getMessage(), e.getCode());
            final byte[] bytes = JSON.toJSONString(error).getBytes(Charset.forName("GBK"));
            final ServerHttpResponse response = exchange.getResponse();
            final DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        }
        return chain.filter(exchange);
    }

}
