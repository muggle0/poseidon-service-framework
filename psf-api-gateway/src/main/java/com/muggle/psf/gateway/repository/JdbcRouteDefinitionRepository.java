package com.muggle.psf.gateway.repository;

import com.alibaba.fastjson.JSONArray;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.muggle.psf.common.constant.ConfigTypeEnum;
import com.muggle.psf.gateway.config.GatewayConfig;
import com.muggle.psf.gateway.properties.PsfGatewayProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Description
 * Date 2023/6/6
 * Created by muggle
 */
@ConditionalOnProperty(prefix = "gateway", name = "api.serializ", havingValue = "jdbc")
@Component
@Slf4j
public class JdbcRouteDefinitionRepository implements RouteDefinitionRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    PsfGatewayProperties properties;

    @Value("${gateway.api.scope}")
    private String scope;

    @Cached(cacheType = CacheType.REMOTE, name = GatewayConfig.JDBC_ROUTE_CACHE_KEY)
    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        try {
            final String sql = "SELECT c.`value` FROM psf_config c WHERE c.`type`='%s' AND c.`scope`='%s'";
            final String secret = jdbcTemplate.queryForObject(String.format(sql, ConfigTypeEnum.GATEWAY.getCode(), scope, "ROUTE"), String.class);
            if (StringUtils.isEmpty(secret)) {
                return Flux.empty();
            }
            final List<RouteDefinition> routeDefinitions = JSONArray.parseArray(secret, RouteDefinition.class);
            return Flux.fromIterable(routeDefinitions);
        } catch (final EmptyResultDataAccessException e) {
            return Flux.empty();
        }
    }

    @Override
    public Mono<Void> save(final Mono<RouteDefinition> route) {
        return null;
    }

    @Override
    public Mono<Void> delete(final Mono<String> routeId) {
        return null;
    }
    // todo
}
