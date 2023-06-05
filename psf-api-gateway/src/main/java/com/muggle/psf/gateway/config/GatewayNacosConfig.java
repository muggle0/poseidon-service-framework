package com.muggle.psf.gateway.config;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.muggle.psf.gateway.properties.PsfGatewayProperties;
import com.muggle.psf.gateway.repository.NacosRouteDefinitionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.Executor;

/**
 * Description
 * Date 2023/6/3
 * Created by muggle
 */
@ConditionalOnProperty(prefix = "gateway", name = "api.serializ", havingValue = "nacos")
@Component
@Slf4j
@DependsOn({"configService"})
public class GatewayNacosConfig implements GatewayConfig {


    @Resource
    private RouteDefinitionRepository routeDefinitionRepository;

    @Resource
    private ConfigService configService;


    @Resource
    private ApplicationEventPublisher publisher;

    @Resource
    private PsfGatewayProperties psfGatewayProperties;

    @Resource
    private NacosRouteDefinitionRepository nacosRouteDefinitionRepository;

    public GatewayNacosConfig() {
        log.info("激活网关配置项》》》》 GatewayNacosConfig");
    }

    @Override
    @PostConstruct
    public void initListener() {
        this.dynamicRouteByNacosListener(psfGatewayProperties.getNacosRouteDateId(), psfGatewayProperties.getNacosRouteGroup());
    }


    private void dynamicRouteByNacosListener(final String dataId, final String group) {
        try {
            //给nacosconfig客户端增加一个监听器
            configService.addListener(dataId, group, new Listener() {
                //自己提供线程池执行操作
                @Override
                public Executor getExecutor() {
                    //为null是默认的线程池
                    return null;
                }

                @Override
                public void receiveConfigInfo(final String config) {
                    publisher.publishEvent(new RefreshRoutesEvent(nacosRouteDefinitionRepository));
                }
            });
        } catch (final NacosException e) {
            log.error("dynamic update gateway config error: {}", e.getMessage(), e);
        }
    }
}
