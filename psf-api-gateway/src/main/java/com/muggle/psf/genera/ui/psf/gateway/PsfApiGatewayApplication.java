package com.muggle.psf.genera.ui.psf.gateway;

import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(exclude = RedissonAutoConfiguration.class)
@EnableDiscoveryClient
public class PsfApiGatewayApplication {
    public static void main(final String[] args) {
        SpringApplication.run(PsfApiGatewayApplication.class, args);
    }
}
