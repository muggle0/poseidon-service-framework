package com.muggle.psf.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

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

    @Value("${gateway.api.cron:*/10 * * * * ?}")
    private String gatewayCron;

    @Value("${gateway.api.key}")
    private String gatewayKey;

    @Resource
    RedissonClient redissonClient;

    @Resource
    private GatewayServer gatewayServer;

    public GatewayReidsConfig() {
        log.info("激活网关配置项》》》》 GatewayReidsConfig");
    }

    @Override
    public void initListener() {
        log.info("GatewayNacosConfig gateway route init ......");
        this.refreshGateway();
    }

    public void refreshGateway() {
        final RBucket<List<RouteDefinition>> bucket = redissonClient.getBucket(gatewayKey);
        if (Objects.isNull(bucket) || !CollectionUtils.isEmpty(bucket.get())) {
            log.debug("refreshGateway list null");
            return;
        }
        gatewayServer.updateList(bucket.get());
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
