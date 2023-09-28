package com.muggle.psf.oauth.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author muggle
 * @Description
 * @createTime 2020-12-18
 */

@SpringBootApplication
@EnableDiscoveryClient
public class PsfOauthApplication {
    public static void main(final String[] args) {
        SpringApplication.run(PsfOauthApplication.class, args);
    }

}
