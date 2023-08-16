package com.muggle.psf.gateway.config;

import com.alibaba.fastjson.JSONArray;
import com.alicp.jetcache.anno.CacheInvalidate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Description
 * Date 2023/6/3
 * Created by muggle
 */
@ConditionalOnProperty(prefix = "gateway", name = "api.serializ", havingValue = "redis")
@Component
@Slf4j
@EnableScheduling
public class GatewayReidsConfig implements GatewayConfig, SchedulingConfigurer {

    @Value("${gateway.api.cron:*/30 * * * * ?}")
    private String gatewayCron;

    private String ROUTE_MD5;

    @Resource
    private RouteDefinitionRepository routeDefinitionRepository;

    @Resource
    private ApplicationEventPublisher publisher;

    public GatewayReidsConfig() {
        log.info("激活网关配置项》》》》 GatewayReidsConfig");
    }

    @Override
    public void run(final String... args) {
        log.info("GatewayNacosConfig gateway route init ......");
        this.refreshGateway();
    }

    @CacheInvalidate(name = GatewayConfig.REDIS_ROUTE_CACHE_KEY)
    public void refreshGateway() {
        final Flux<RouteDefinition> routeDefinitions = routeDefinitionRepository.getRouteDefinitions();
        final List<RouteDefinition> routeList = routeDefinitions.collectList().block();
        if (Objects.isNull(ROUTE_MD5) && CollectionUtils.isEmpty(routeList)) {
            return;
        }
        if (Objects.isNull(ROUTE_MD5) && !CollectionUtils.isEmpty(routeList)) {
            publisher.publishEvent(new RefreshRoutesEvent(routeDefinitionRepository));
            ROUTE_MD5 = DigestUtils.md5Hex(JSONArray.toJSONString(routeList));
            return;
        }
        final String redisMD5 = DigestUtils.md5Hex(JSONArray.toJSONString(routeList));
        if (!Objects.equals(redisMD5, ROUTE_MD5)) {
            publisher.publishEvent(new RefreshRoutesEvent(routeDefinitionRepository));
            ROUTE_MD5 = redisMD5;
        }
    }

    @Override
    public void configureTasks(final ScheduledTaskRegistrar scheduledTaskRegistrar) {
        //任务线程，在run方法中添加业务逻辑
        final Runnable task = () -> {
            log.debug("GatewayReidsConfig refreshGateway ......");
            this.refreshGateway();
        };
        final Trigger trigger = triggerContext -> {
            final CronTrigger cronTrigger = new CronTrigger(gatewayCron);
            final Date nextExecTime = cronTrigger.nextExecutionTime(triggerContext);
            return nextExecTime;
        };
        scheduledTaskRegistrar.addTriggerTask(task, trigger);
    }
}
