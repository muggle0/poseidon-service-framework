package com.muggle.psf.config;

import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Description
 * Date 2023/6/3
 * Created by muggle
 */
@ConditionalOnProperty(prefix = "gateway", name = "api.serializ", havingValue = "redis")
@Component
public class GatewayRedissonAutoConfig extends RedissonAutoConfiguration {
}
