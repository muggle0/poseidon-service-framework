package com.muggle.psf.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Description
 * Date 2023/6/2
 * Created by muggle
 */
@Configuration
@Getter
public class PsfGatewayProperties {

    @Value("${nacos.gateway.route.config.timeout:30000}")
    private    long defaultTimeout = 30000;

    @Value("${spring.cloud.nacos.discovery.server-addr}")
    private  String nacosServerAddr;

    @Value("${spring.cloud.nacos.discovery.namespace}")
    private  String nacosNamespace;

    @Value("${nacos.gateway.route.config.data-id}")
    private  String nacosRouteDateId;

    @Value("${nacos.gateway.route.config.group}")
    private  String nacosRouteGroup;


}
