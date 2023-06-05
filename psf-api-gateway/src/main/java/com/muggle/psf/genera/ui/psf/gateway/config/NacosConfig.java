package com.muggle.psf.genera.ui.psf.gateway.config;

import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.nacos.api.config.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Description
 * Date 2023/6/3
 * Created by muggle
 */
@Configuration
@ConditionalOnProperty(name = "spring.cloud.nacos.config.enabled", matchIfMissing = true)
@Slf4j
public class NacosConfig {


    @Bean
    public ConfigService configService(final ApplicationContext context) {
        log.info("NacosConfig init ConfigService 》》》》");
        if (context.getParent() != null && BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
            context.getParent(), NacosConfigProperties.class).length > 0) {
            final NacosConfigProperties properties = BeanFactoryUtils.beanOfTypeIncludingAncestors(context.getParent(),
                NacosConfigProperties.class);
            return properties.configServiceInstance();
        }
        return new NacosConfigProperties().configServiceInstance();
    }
}
