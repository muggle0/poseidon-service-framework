package com.muggle.psf.gateway;

import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(exclude = RedissonAutoConfiguration.class)
@EnableDiscoveryClient
@EnableCreateCacheAnnotation
@EnableMethodCache(basePackages = "com.muggle.psf.gateway")
public class PsfApiGatewayApplication {
    public static void main(final String[] args) {
        SpringApplication.run(PsfApiGatewayApplication.class, args);
    }
}
