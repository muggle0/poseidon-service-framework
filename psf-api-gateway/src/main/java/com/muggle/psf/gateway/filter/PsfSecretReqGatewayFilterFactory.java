package com.muggle.psf.gateway.filter;

import com.muggle.psf.common.SecretKeyUtils;
import com.muggle.psf.common.exception.GatewayException;
import com.muggle.psf.gateway.properties.PsfHeadkeyProperties;
import com.muggle.psf.gateway.service.SecretService;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.GatewayToStringStyler;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.reactive.function.server.DefaultServerRequest;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

/**
 * Description
 * Date 2023/6/13
 * Created by muggle
 */
@Component
@Slf4j
public class PsfSecretReqGatewayFilterFactory extends AbstractGatewayFilterFactory {

    private final SecretService secretService;

    private final PsfHeadkeyProperties psfHeadkeyProperties;

    private final List<HttpMessageReader<?>> messageReaders;

    public PsfSecretReqGatewayFilterFactory(SecretService secretService, PsfHeadkeyProperties psfHeadkeyProperties) {
        this.secretService = secretService;
        this.psfHeadkeyProperties = psfHeadkeyProperties;
        this.messageReaders = HandlerStrategies.withDefaults().messageReaders();
    }

    @Override
    public GatewayFilter apply(final Object config) {
        return new PsfSecretGatewayFilter(psfHeadkeyProperties);
    }

    public class PsfSecretGatewayFilter extends BaseGatewayFilter implements GatewayFilter, Ordered {
        public PsfSecretGatewayFilter(final PsfHeadkeyProperties properties) {
            super(properties);
        }

        //注意此处一定要比-1小
        @Override
        public int getOrder() {
            return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER - 1;
        }

        @Override
        public void beforeProcess(final ServerWebExchange exchange) {
            log.info("start PsfSecretGatewayFilter");
            final String secret = exchange.getRequest().getHeaders().getFirst(psfHeadkeyProperties.getAppsecret());
            final String appip = exchange.getRequest().getHeaders().getFirst(psfHeadkeyProperties.getAppid());
            final String nonce = exchange.getRequest().getHeaders().getFirst(psfHeadkeyProperties.getNonce());
            if (StringUtils.isEmpty(secret) || StringUtils.isEmpty(appip) || StringUtils.isEmpty(nonce)) {
                throw new GatewayException("网关凭证错误");
            }
            final String secertByAppId = secretService.getSecertByAppId(appip, nonce);
            if (!Objects.equals(secertByAppId, secret)) {
                throw new GatewayException("网关凭证错误");
            }
        }

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            return new GatewayFilter() {
                public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                    ServerRequest serverRequest = ServerRequest.create(exchange,messageReaders);
                    Mono<?> modifiedBody = serverRequest.bodyToMono(String.class).flatMap((originalBody) -> {
                        new RewriteFunction<String,String>(){

                            @Override
                            public Publisher<String> apply(ServerWebExchange serverWebExchange, String s) {
                                return null;
                            }
                        };
                        return config.getRewriteFunction().apply(exchange, originalBody);
                    }).switchIfEmpty(Mono.defer(() -> {
                        return (Mono)config.getRewriteFunction().apply(exchange, (Object)null);
                    }));
                    BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody, config.getOutClass());
                    HttpHeaders headers = new HttpHeaders();
                    headers.putAll(exchange.getRequest().getHeaders());
                    headers.remove("Content-Length");
                    if (config.getContentType() != null) {
                        headers.set("Content-Type", config.getContentType());
                    }

                    CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, headers);
                    return bodyInserter.insert(outputMessage, new BodyInserterContext()).then(Mono.defer(() -> {
                        ServerHttpRequest decorator = ModifyRequestBodyGatewayFilterFactory.this.decorate(exchange, headers, outputMessage);
                        return chain.filter(exchange.mutate().request(decorator).build());
                    })).onErrorResume((throwable) -> {
                        return ModifyRequestBodyGatewayFilterFactory.this.release(exchange, outputMessage, throwable);
                    });
                }

                public String toString() {
                    return GatewayToStringStyler.filterToStringCreator(ModifyRequestBodyGatewayFilterFactory.this).append("Content type", config.getContentType()).append("In class", config.getInClass()).append("Out class", config.getOutClass()).toString();
                }
            };
        }
    }


}
