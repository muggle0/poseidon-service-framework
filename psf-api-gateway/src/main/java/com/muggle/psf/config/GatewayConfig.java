package com.muggle.psf.config;

import javax.annotation.PostConstruct;

/**
 * Description 网关配置接口定义
 * Date 2023/6/3
 * Created by muggle
 */
public interface GatewayConfig {

    @PostConstruct
    void initListener();

}
