package com.muggle.psf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class PsfApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(PsfApiGatewayApplication.class, args);
    }
}
