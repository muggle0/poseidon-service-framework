package com.muggle.psf.gateway.config;

import org.springframework.boot.CommandLineRunner;

/**
 * Description 网关配置接口定义
 * Date 2023/6/3
 * Created by muggle
 */
public interface GatewayConfig extends CommandLineRunner {

    String NACOS_ROUTE_CACHE_KEY = "nacos:route:gateway:cache";

    String REDIS_ROUTE_CACHE_KEY = "redis:route:gateway:cache";

    String JDBC_ROUTE_CACHE_KEY = "jdbc:route:gateway:cache";

}
