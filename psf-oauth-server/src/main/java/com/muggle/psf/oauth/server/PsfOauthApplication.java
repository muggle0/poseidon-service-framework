package com.muggle.psf.oauth.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

/**
 * @author muggle
 * @Description
 * @createTime 2020-12-18
 */

@SpringBootApplication
@EnableDiscoveryClient
@EnableAuthorizationServer
public class PsfOauthApplication {
    public static void main(final String[] args) {
        SpringApplication.run(PsfOauthApplication.class, args);
    }

}
