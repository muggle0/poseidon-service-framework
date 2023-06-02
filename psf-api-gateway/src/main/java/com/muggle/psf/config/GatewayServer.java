package com.muggle.psf.config;

import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Description spring提供的事件推送接口
 * Date 2023/6/2
 * Created by muggle
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GatewayServer  implements ApplicationEventPublisherAware {

    /* 写gateway中的路由定义 */
    private final RouteDefinitionWriter routeDefinitionWriter;

    /* 获取路由定义 */
    private final RouteDefinitionLocator routeDefinitionLocator;

    /* 事件发布对象 */
    private ApplicationEventPublisher publisher;


    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        //完成时间推送句柄的初始化
        this.publisher = applicationEventPublisher;
    }

    /**
     * @Description 添加路由定义
     * @Params [definition]
     * @Return java.lang.String
     * @Author JiaChaoYang
     * @Date 2022/9/12 9:24
     */
    public String addRouteDefinition(RouteDefinition definition){
        log.info("gateway add route: {}",definition);
        /* 保存路由配置并发布 */
        routeDefinitionWriter.save(Mono.just(definition)).subscribe();
        /* 发布事件通知给Gateway  同步新增路由定义 */
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
        return "success";
    }

    /**
     * @Description 根据路由id去删除路由配置
     * @Params [id]
     * @Return java.lang.String
     * @Author JiaChaoYang
     * @Date 2022/9/12 9:29
     */
    private String deleteById(String id){
        try {
            log.info("gateway delete route id : {}",id);
            this.routeDefinitionWriter.delete(Mono.just(id));
            //发布事件通知给gateway 更新路由定义
            this.publisher.publishEvent(new RefreshRoutesEvent(this));
            return "delete success";
        }catch (Exception e) {
            log.error("gateway delete route fail: {}",e.getMessage(),e);
            return "delete fail";
        }
    }

    /**
     * @Description 更新路由
     * @Params [routeDefinitionList]
     * @Return java.lang.String
     * @Author JiaChaoYang
     * @Date 2022/9/12 9:36
     */
    public String  updateList(List<RouteDefinition> routeDefinitionList){
        log.info("gateway update route: {}",routeDefinitionList);

        //拿到当前gateway 中存储的路由定义
        List<RouteDefinition> routeDefinitions = routeDefinitionLocator.getRouteDefinitions().buffer().blockFirst();
        if (!CollectionUtils.isEmpty(routeDefinitions)){
            //清除掉之前所有的旧的路由定义
            routeDefinitions.forEach(rd ->{
                log.info("delete route definition:",rd);
                deleteById(rd.getId());
            });
        }
        // 把更新的路由定义同步到gateway中
        routeDefinitionList.forEach(definition -> updateByRouteDefinition(definition));
        return "success";
    }

    /**
     * @Description 更新路由，更新的实现策略比较简单：删除 + 新增 = 更新
     * @Params [definition]
     * @Return java.lang.String
     * @Author JiaChaoYang
     * @Date 2022/9/12 9:33
     */
    private String updateByRouteDefinition(RouteDefinition definition){
        try {
            log.info("gateway update route : {}",definition);
            this.routeDefinitionWriter.delete(Mono.just(definition.getId()));
        }catch (Exception e) {
            return "update fail , not find route routeId:"+ definition.getId();
        }
        try {
            this.routeDefinitionWriter.save(Mono.just(definition)).subscribe();
            this.publisher.publishEvent(new RefreshRoutesEvent(this));
            return "success";
        }catch (Exception e) {
            return "update route fail";
        }
    }
}
