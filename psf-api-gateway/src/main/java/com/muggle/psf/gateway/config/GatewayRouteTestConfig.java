package com.muggle.psf.gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.muggle.psf.gateway.filter.ResponseBodyRewrite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.HandlerResult;
import org.springframework.web.reactive.accept.RequestedContentTypeResolver;
import org.springframework.web.reactive.result.method.annotation.ResponseBodyResultHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Description
 * Date 2023/6/6
 * Created by muggle
 */
@Configuration
public class GatewayRouteTestConfig {

    @Autowired
    private ApplicationEventPublisher publisher;


    //    @Bean
    public RouteLocator routeLocator(final RouteLocatorBuilder builder) {
        final RouteLocator poseidon = builder.routes()
            .route("poseidon", r ->
                r.path("/**").uri("http://127.0.0.1:9000"))
            .build();
        return poseidon;
    }

    //    @Bean
    public RouteLocator routes(final RouteLocatorBuilder builder, final ObjectMapper objectMapper, final MessageSource messageSource) {
        final RouteLocator path_route_change = builder
            .routes()
            .route("test",
                r -> r.path("/hello/change")
                    .filters(f -> f
                        .modifyResponseBody(String.class, String.class, new ResponseBodyRewrite(objectMapper, messageSource))
                    )
                    .uri("http://127.0.0.1:8082"))
            .build();
        return path_route_change;
    }

    /*@Bean
    public RouteLocator routes(final RouteLocatorBuilder builder, final ObjectMapper objectMapper, final MessageSource messageSource) {
        final RouteLocator path_route_change = builder
            .routes()
            .route(r -> r.path("/**").filters(f -> f.modifyResponseBody(String.class, String.class,
                new ResponseBodyRewrite(objectMapper, messageSource)))
                .uri("/**"))
            .build();
        return path_route_change;
    }*/

    //    @Bean
    public GlobalResponseBodyHandler responseWrapper(final ServerCodecConfigurer serverCodecConfigurer,
                                                     final RequestedContentTypeResolver requestedContentTypeResolver) {
        return new GlobalResponseBodyHandler(serverCodecConfigurer.getWriters(), requestedContentTypeResolver);
    }

    public static class GlobalResponseBodyHandler extends ResponseBodyResultHandler {
        public GlobalResponseBodyHandler(final List<HttpMessageWriter<?>> writers, final RequestedContentTypeResolver resolver) {
            super(writers, resolver);
        }

        public GlobalResponseBodyHandler(final List<HttpMessageWriter<?>> writers, final RequestedContentTypeResolver resolver, final ReactiveAdapterRegistry registry) {
            super(writers, resolver, registry);
        }

        @Override
        @SuppressWarnings("unchecked")
        public Mono<Void> handleResult(final ServerWebExchange exchange, final HandlerResult result) {
            final Object body = result.getReturnValue();
            final MethodParameter bodyTypeParameter = result.getReturnTypeSource();
            return this.writeBody(body, bodyTypeParameter, exchange);
        }
    }

}
