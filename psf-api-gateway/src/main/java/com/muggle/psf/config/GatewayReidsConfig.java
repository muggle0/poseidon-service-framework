package com.muggle.psf.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

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

    @Value("${gateway.api.serializ.cron:*/10 * * * * ?}")
    private String gatewayCron;

    @Resource
    RedissonClient redissonClient;

    public GatewayReidsConfig() {
        log.info("激活网关配置项》》》》 GatewayReidsConfig");
    }

    @Override
    public void initListener() {
        log.info("GatewayNacosConfig gateway route init ......");
        this.refreshGateway();
    }

    public void refreshGateway() {

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
