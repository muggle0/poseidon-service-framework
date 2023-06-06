package com.muggle.psf.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;

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

}
