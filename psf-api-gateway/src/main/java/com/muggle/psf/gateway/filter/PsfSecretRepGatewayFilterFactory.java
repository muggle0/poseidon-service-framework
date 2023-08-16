package com.muggle.psf.gateway.filter;

import com.alibaba.fastjson.JSONObject;
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
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.cloud.gateway.support.CachedBodyOutputMessage;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.function.BiFunction;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

/**
 * Description
 * Date 2023/6/13
 * Created by muggle
 */
@Component
@Slf4j
public class PsfSecretRepGatewayFilterFactory extends AbstractGatewayFilterFactory {

    @Autowired
    private SecretService secretService;

    @Autowired
    private PsfHeadkeyProperties psfHeadkeyProperties;

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
            log.info("start PsfSecretRepGatewayFilterFactory");
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
        protected Mono<Void> afterProcess(final ServerWebExchange exchange, final GatewayFilterChain chain) {
            return chain.filter(exchange.mutate().response(this.encodeResp(exchange)).build());
        }

        @SuppressWarnings("unchecked")
        private ServerHttpResponse encodeResp(final ServerWebExchange exchange) {
            return new ServerHttpResponseDecorator(exchange.getResponse()) {
                @Override
                public Mono<Void> writeWith(final Publisher<? extends DataBuffer> body) {
                    final String originalResponseContentType = exchange
                        .getAttribute(ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR);
                    final HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.add(CONTENT_TYPE,
                        originalResponseContentType);
                    final ClientResponse clientResponse = ClientResponse
                        .create(exchange.getResponse().getStatusCode())
                        .headers(headers -> headers.putAll(httpHeaders))
                        .body(Flux.from(body)).build();
                    final Mono<String> modifiedBody = clientResponse.bodyToMono(String.class)
                        .flatMap(originalBody -> this.modifyBody(exchange.getRequest().getHeaders().getFirst(psfHeadkeyProperties.getAppsecret()))
                            .apply(exchange, Mono.just(originalBody)));
                    final BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody,
                        String.class);
                    final CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(
                        exchange, exchange.getResponse().getHeaders());
                    return bodyInserter.insert(outputMessage, new BodyInserterContext())
                        .then(Mono.defer(() -> {
                            Flux<DataBuffer> messageBody = outputMessage.getBody();
                            final HttpHeaders headers = this.getDelegate().getHeaders();
                            if (!headers.containsKey(HttpHeaders.TRANSFER_ENCODING)) {
                                messageBody = messageBody.doOnNext(data -> headers
                                    .setContentLength(data.readableByteCount()));
                            }
                            return this.getDelegate().writeWith(messageBody);
                        }));
                }

                private BiFunction<ServerWebExchange, Mono<String>, Mono<String>> modifyBody(final String secret) {
                    return (exchange, json) -> {
                        if (StringUtils.isEmpty(json.block())) {
                            return json;
                        }
                        try {
                            final JSONObject jsonObject = JSONObject.parseObject(json.block());
                            final String code = jsonObject.get("code").toString();
                            if (StringUtils.isEmpty(code)) {
                                return json;
                            }
                            final Object data = jsonObject.get("data");
                            if (Objects.isNull(data)) {
                                return json;
                            }
                            final String authData = SecretKeyUtils.encryptFromString(data.toString(), secret);
                            jsonObject.put("data", authData);
                            return Mono.just(jsonObject.toJSONString());
                        } catch (final Exception e) {
                            log.error("ResponseMessageGatewayFilterFactory modifyBody error", e);
                            return json;
                        }
                    };
                }

                @Override
                public Mono<Void> writeAndFlushWith(
                    final Publisher<? extends Publisher<? extends DataBuffer>> body) {
                    return this.writeWith(Flux.from(body).flatMapSequential(p -> p));
                }
            };
        }

    }


}
