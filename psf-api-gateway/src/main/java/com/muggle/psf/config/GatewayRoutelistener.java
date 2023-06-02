package com.muggle.psf.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Description
 * Date 2023/6/2
 * Created by muggle
 */
@Slf4j
@RequiredArgsConstructor
@Component
@DependsOn({"configService"})
public class GatewayRoutelistener {
    /* Nacos配置服务客户端 */
    private final ConfigService configService;

    private final GatewayServer gatewayServer;

    private final PsfGatewayProperties psfGatewayProperties;

    /**
     * @Description 在容器中构造完成后会立即执行
     * @Params []
     * @Return void
     * @Author JiaChaoYang
     * @Date 2022/9/13 11:54
     */
    @PostConstruct //bean构造完成后，会立即执行
    public void init(){
        log.info("gateway route init ......");
        try {
            String configInfo = configService.getConfig(
                psfGatewayProperties.getNacosRouteDateId(),
                psfGatewayProperties.getNacosRouteGroup(),
                psfGatewayProperties.getDefaultTimeout()
            );
            log.info("get current gateway config : {}",configInfo);
            List<RouteDefinition> definitionList = JSON.parseArray(configInfo,RouteDefinition.class);
            if (!CollectionUtils.isEmpty(definitionList)){
                for (RouteDefinition definitionDefinition : definitionList){
                    log.info("init gateway config {}",definitionDefinition.toString());
                    gatewayServer.addRouteDefinition(definitionDefinition);
                }
            }
        }catch (Exception e) {
            log.error("gateway route init has some error: {}",e.getMessage(),e);
        }
        //设置监听器
        dynamicRouteByNacosListener(psfGatewayProperties.getNacosRouteDateId(), psfGatewayProperties.getNacosRouteGroup());
    }


    /**
     * @Description 实现对nacos的监听，nacos下发的动态路由配置信息
     * @Params [dataId, group]
     * @Return void
     * @Author JiaChaoYang
     * @Date 2022/9/13 11:17
     */
    private void dynamicRouteByNacosListener(String dataId , String group){
        try {

            //给nacosconfig客户端增加一个监听器
            configService.addListener(dataId, group, new Listener() {
                //自己提供线程池执行操作
                @Override
                public Executor getExecutor() {
                    //为null是默认的线程池
                    return null;
                }
                /**
                 * @Description 监听器收到配置更新
                 * @Params [s] nacos中最新的配置定义
                 * @Return void
                 * @Author JiaChaoYang
                 * @Date 2022/9/13 11:21
                 */
                @Override
                public void receiveConfigInfo(String s) {
                    log.info("start to update config : ",s);
                    //接收最新的路由定义配置
                    List<RouteDefinition> definitionList = JSON.parseArray(s,RouteDefinition.class);
                    log.info("update route : {}",definitionList.toString());
                    //更新路由配置
                    gatewayServer.updateList(definitionList);
                }
            });
        }catch (NacosException e) {
            log.error("dynamic update gateway config error: {}",e.getMessage(),e);
        }
    }
}
