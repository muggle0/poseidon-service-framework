package com.muggle.psf.gateway.filter;

import com.muggle.psf.common.SecretKeyUtils;
import com.muggle.psf.common.exception.GatewayException;
import com.muggle.psf.gateway.properties.PsfHeadkeyProperties;
import com.muggle.psf.gateway.service.SecretService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.codec.DecoderHttpMessageReader;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
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

    private static final HttpMessageReader messageReader = new DecoderHttpMessageReader<>(new Jackson2JsonDecoder());


    public PsfSecretReqGatewayFilterFactory(SecretService secretService, PsfHeadkeyProperties psfHeadkeyProperties) {
        this.secretService = secretService;
        this.psfHeadkeyProperties = psfHeadkeyProperties;
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
        protected Mono<Void> afterProcess(ServerWebExchange exchange, GatewayFilterChain chain) {
            return this.decodeReque(exchange, chain);
        }

        private Mono<Void> decodeReque(final ServerWebExchange exchange, final GatewayFilterChain chain) {
            ServerHttpRequest request = exchange.getRequest();
            // mediaType
            final MediaType mediaType = exchange.getRequest().getHeaders().getContentType();
            final HttpMethod method = exchange.getRequest().getMethod();
            Flux<String> modifiedBody = request.getBody().map(dataBuffer -> {
                byte[] bytes = new byte[dataBuffer.readableByteCount()];
                dataBuffer.read(bytes);
                DataBufferUtils.release(dataBuffer);
                return new String(bytes, StandardCharsets.UTF_8);
            }).flatMap(body -> {
                if (MediaType.APPLICATION_JSON.isCompatibleWith(mediaType) && Objects.equals(method, HttpMethod.POST)) {
                    final String decryptFromString = SecretKeyUtils.decryptFromString(body, exchange.getRequest().getHeaders().getFirst(psfHeadkeyProperties.getAppsecret()));
                    return Mono.just(decryptFromString);
                }
                return Mono.empty();
            });
            final BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody, String.class);
            final HttpHeaders headers = new HttpHeaders();
            headers.putAll(exchange.getRequest().getHeaders());
            headers.remove(HttpHeaders.CONTENT_LENGTH);
            final CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, headers);
            return bodyInserter.insert(outputMessage, new BodyInserterContext()).then(Mono.defer(() -> {
                final ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(exchange.getRequest()) {
                    @Override
                    public HttpHeaders getHeaders() {
                        final long contentLength = headers.getContentLength();
                        final HttpHeaders httpHeaders = new HttpHeaders();
                        httpHeaders.putAll(super.getHeaders());
                        if (contentLength > 0) {
                            httpHeaders.setContentLength(contentLength);
                        } else {
                            httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                        }
                        return httpHeaders;
                    }

                    @Override
                    public Flux<DataBuffer> getBody() {
                        return outputMessage.getBody();
                    }
                };
                return chain.filter(exchange.mutate().request(decorator).build());
            }));
        }
    }


}
