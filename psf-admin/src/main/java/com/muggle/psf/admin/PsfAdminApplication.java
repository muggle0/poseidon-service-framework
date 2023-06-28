package com.muggle.psf.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author muggle
 * @Description
 * @createTime 2020-12-18
 */

@SpringBootApplication
@EnableAsync
@EnableDiscoveryClient
@EnableScheduling
public class PsfAdminApplication {
    public static void main(final String[] args) {
        SpringApplication.run(PsfAdminApplication.class, args);
    }


}
