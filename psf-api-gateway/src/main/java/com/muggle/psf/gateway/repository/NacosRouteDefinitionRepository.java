package com.muggle.psf.gateway.repository;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.muggle.psf.gateway.config.GatewayConfig;
import com.muggle.psf.gateway.properties.PsfGatewayProperties;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Description
 * Date 2023/6/4
 * Created by muggle
 */
@ConditionalOnProperty(prefix = "gateway", name = "api.serializ", havingValue = "nacos")
@Component
@Slf4j
public class NacosRouteDefinitionRepository implements RouteDefinitionRepository {

    @Resource
    private ConfigService configService;

    @Resource
    private PsfGatewayProperties psfGatewayProperties;


    @Override
    @Cached(cacheType = CacheType.LOCAL, name = GatewayConfig.NACOS_ROUTE_CACHE_KEY)
    public Flux<RouteDefinition> getRouteDefinitions() {
        try {
            final String configInfo = configService.getConfig(psfGatewayProperties.getNacosRouteDateId(),
                psfGatewayProperties.getNacosRouteGroup(), psfGatewayProperties.getDefaultTimeout());
            log.info("get current gateway config : {}", configInfo);
            final List<RouteDefinition> definitionList = JSON.parseArray(configInfo, RouteDefinition.class);
            return Flux.fromIterable(definitionList);
        } catch (final NacosException e) {
            log.error("gateway route getRouteDefinitions has some error: {}", e.getMessage(), e);
            return Flux.empty();
        }
    }

    @Override
    public Mono<Void> save(final Mono<RouteDefinition> route) {
        try {
            final RouteDefinition block = route.block();
            if (Objects.isNull(block)) {
                return Mono.empty();
            }
            final String configInfo = configService.getConfig(psfGatewayProperties.getNacosRouteDateId(),
                psfGatewayProperties.getNacosRouteGroup(), psfGatewayProperties.getDefaultTimeout());
            final List<RouteDefinition> definitionList = JSON.parseArray(configInfo, RouteDefinition.class);
            boolean flag = true;
            for (final RouteDefinition routeDefinition : definitionList) {
                if (Objects.equals(routeDefinition.getId(), block.getId())) {
                    BeanUtils.copyProperties(block, routeDefinition);
                    flag = false;
                }
            }
            if (flag) {
                definitionList.add(block);
            }
            configService.publishConfig(psfGatewayProperties.getNacosRouteDateId(), psfGatewayProperties.getNacosRouteGroup()
                , JSONArray.toJSONString(definitionList));
        } catch (final NacosException e) {
            log.error("gateway route save has some error: {}", e.getMessage(), e);
        }
        return Mono.empty();
    }

    @Override
    public Mono<Void> delete(final Mono<String> routeId) {
        final String configInfo;
        try {
            configInfo = configService.getConfig(psfGatewayProperties.getNacosRouteDateId(),
                psfGatewayProperties.getNacosRouteGroup(), psfGatewayProperties.getDefaultTimeout());
        } catch (final NacosException e) {
            log.error("gateway route delete has some error: {}", e.getMessage(), e);
            return Mono.empty();
        }
        final List<RouteDefinition> definitionList = JSON.parseArray(configInfo, RouteDefinition.class);
        final Iterator<RouteDefinition> iterator = definitionList.iterator();
        while (iterator.hasNext()) {
            final RouteDefinition next = iterator.next();
            if (Objects.equals(next.getId(), routeId.block())) {
                iterator.remove();
            }
        }
        return Mono.empty();
    }

    @CacheInvalidate(name = GatewayConfig.NACOS_ROUTE_CACHE_KEY)
    public void invalidateCache() {
        log.info("NacosRouteDefinitionRepository invalidateCache 》》》{} ", LocalDateTime.now());
    }
}
