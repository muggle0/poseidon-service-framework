package com.muggle.psf.admin;

import com.muggle.psf.genera.ui.controller.CodeController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
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

    @Profile("local")
    @Bean
    public CodeController codeController() {
        return new CodeController();
    }

}
