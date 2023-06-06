package com.muggle.psf.gateway.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.route.InMemoryRouteDefinitionRepository;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Description
 * Date 2023/6/6
 * Created by muggle
 */
@ConditionalOnProperty(prefix = "gateway", name = "api.serializ", havingValue = "test")
@Component
@Slf4j
public class TestRouteDefinitionRepository extends InMemoryRouteDefinitionRepository {
    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        return super.getRouteDefinitions();
    }

    @Override
    public Mono<Void> save(final Mono<RouteDefinition> route) {
        return super.save(route);
    }

    @Override
    public Mono<Void> delete(final Mono<String> routeId) {
        return super.delete(routeId);
    }
}
