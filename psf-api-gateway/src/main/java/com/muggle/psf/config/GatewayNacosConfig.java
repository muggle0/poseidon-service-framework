package com.muggle.psf.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
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
    private ConfigService configService;

    @Resource
    private GatewayServer gatewayServer;

    @Resource
    private PsfGatewayProperties psfGatewayProperties;

    public GatewayNacosConfig() {
        log.info("激活网关配置项》》》》 GatewayNacosConfig");
    }

    @Override
    @PostConstruct
    public void initListener() {
        log.info("GatewayNacosConfig gateway route init ......");
        try {
            final String configInfo = configService.getConfig(
                psfGatewayProperties.getNacosRouteDateId(),
                psfGatewayProperties.getNacosRouteGroup(),
                psfGatewayProperties.getDefaultTimeout()
            );
            log.info("get current gateway config : {}", configInfo);
            final List<RouteDefinition> definitionList = JSON.parseArray(configInfo, RouteDefinition.class);
            if (!CollectionUtils.isEmpty(definitionList)) {
                for (final RouteDefinition definitionDefinition : definitionList) {
                    log.info("init gateway config {}", definitionDefinition.toString());
                    gatewayServer.addRouteDefinition(definitionDefinition);
                }
            }
        } catch (final Exception e) {
            log.error("gateway route init has some error: {}", e.getMessage(), e);
        }
        //设置监听器
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
                public void receiveConfigInfo(final String s) {
                    log.info("start to update config : ", s);
                    //接收最新的路由定义配置
                    final List<RouteDefinition> definitionList = JSON.parseArray(s, RouteDefinition.class);
                    log.info("update route : {}", definitionList.toString());
                    //更新路由配置
                    gatewayServer.updateList(definitionList);
                }
            });
        } catch (final NacosException e) {
            log.error("dynamic update gateway config error: {}", e.getMessage(), e);
        }
    }
}
