package com.muggle.psf.config;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * Description
 * Date 2023/6/3
 * Created by muggle
 */
@Configuration
@ConditionalOnProperty(name = "spring.cloud.nacos.config.enabled", matchIfMissing = true)
public class NacosConfig {


    @Bean
    public ConfigService configService() throws NacosException {
        final Properties properties = new Properties();
        final ConfigService configService = NacosFactory.createConfigService(properties);

        return configService;

    }
}
