package com.muggle.psf.gateway.repository;

import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.muggle.psf.gateway.config.GatewayConfig;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Objects;

/**
 * Description
 * Date 2023/6/4
 * Created by muggle
 */
@ConditionalOnProperty(prefix = "gateway", name = "api.serializ", havingValue = "redis")
@Component
@Slf4j
public class RedisRouteDefinitionRepository implements RouteDefinitionRepository {

    @Autowired
    private RedissonClient redissonClient;


    @Value("${gateway.api.key}")
    private String gatewayKey;

    @Cached(cacheType = CacheType.LOCAL, name = GatewayConfig.REDIS_ROUTE_CACHE_KEY)
    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        final RMap<String, RouteDefinition> routeMap = redissonClient.getMap(gatewayKey);
        if (Objects.isNull(routeMap)) {
            log.debug("refreshGateway list null");
            return Flux.empty();
        }
        final Collection<RouteDefinition> ruotes = routeMap.values();
        return Flux.fromIterable(ruotes);
    }

    @Override
    public Mono<Void> save(final Mono<RouteDefinition> route) {
        final RMap<String, RouteDefinition> routeMap = redissonClient.getMap(gatewayKey);
        final String id = route.block().getId();
        routeMap.put(id, route.block());
        return Mono.empty();
    }

    @Override
    public Mono<Void> delete(final Mono<String> routeId) {
        final RMap<String, RouteDefinition> routeMap = redissonClient.getMap(gatewayKey);
        routeMap.remove(routeId.block());
        return Mono.empty();
    }
}
