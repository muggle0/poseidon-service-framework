package com.muggle.psf.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.muggle.psf.gateway.properties.PsfHeadkeyProperties;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.context.MessageSource;
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

import java.util.function.BiFunction;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR;

/**
 * Description
 * Date 2023/6/13
 * Created by muggle
 */
@Component
@Slf4j
public class ResponseMessageGatewayFilterFactory extends AbstractGatewayFilterFactory {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private PsfHeadkeyProperties psfHeadkeyProperties;

    @Override
    public GatewayFilter apply(final Object config) {
        return new MessageSourceGatewayFilter(psfHeadkeyProperties);
    }

    public class MessageSourceGatewayFilter extends BaseGatewayFilter implements GatewayFilter, Ordered {
        public MessageSourceGatewayFilter(final PsfHeadkeyProperties properties) {
            super(properties);
        }

        //注意此处一定要比-1小
        @Override
        public int getOrder() {
            return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER - 1;
        }

        @Override
        public void beforeProcess(final ServerWebExchange exchange) {
            log.info("start MessageSourceGatewayFilter");
        }


        @Override
        protected Mono<Void> afterProcess(final ServerWebExchange exchange, final GatewayFilterChain chain) {
            return chain.filter(exchange.mutate().response(this.decorate(exchange)).build());
        }

        @SuppressWarnings("unchecked")
        private ServerHttpResponse decorate(final ServerWebExchange exchange) {
            return new ServerHttpResponseDecorator(exchange.getResponse()) {
                @Override
                public Mono<Void> writeWith(final Publisher<? extends DataBuffer> body) {
                    final String originalResponseContentType = exchange
                        .getAttribute(ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR);
                    final HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.add(HttpHeaders.CONTENT_TYPE,
                        originalResponseContentType);
                    final ClientResponse clientResponse = ClientResponse
                        .create(exchange.getResponse().getStatusCode())
                        .headers(headers -> headers.putAll(httpHeaders))
                        .body(Flux.from(body)).build();
                    final Mono<String> modifiedBody = clientResponse.bodyToMono(String.class)
                        .flatMap(originalBody -> this.modifyBody()
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

                private BiFunction<ServerWebExchange, Mono<String>, Mono<String>> modifyBody() {
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
                            final String message = messageSource.getMessage(code, null, jsonObject.get("message").toString(), exchange.getLocaleContext().getLocale());
                            jsonObject.put("message", message);
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
