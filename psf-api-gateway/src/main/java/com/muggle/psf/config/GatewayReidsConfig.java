package com.muggle.psf.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Description
 * Date 2023/6/3
 * Created by muggle
 */
@ConditionalOnProperty(prefix = "gateway", name = "api.serializ", havingValue = "redis")
@Component
@Slf4j
public class GatewayReidsConfig implements GatewayConfig {

    public GatewayReidsConfig() {
        log.info("激活网关配置项》》》》 GatewayReidsConfig");
    }

    @Override
    public void initListener() {
        log.info("GatewayNacosConfig gateway route init ......");
    }
}
