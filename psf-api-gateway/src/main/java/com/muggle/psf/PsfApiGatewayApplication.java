package com.muggle.psf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;

@SpringBootApplication
@EnableZuulProxy
@EnableOAuth2Sso
public class PsfApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(PsfApiGatewayApplication.class, args);
    }

}
