package com.muggle.psf.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.muggle.psf.common.result.ResultBean;
import com.muggle.psf.gateway.properties.PsfHeadkeyProperties;
import com.muggle.psf.gateway.service.AuthService;
import com.muggle.psf.gateway.service.BlackListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;

/**
 * Description
 * Date 2023/6/7
 * Created by muggle
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "gateway", name = "api.auth.enabled", havingValue = "true", matchIfMissing = true)
@Order(2)
public class AuthFilter extends BaseGatewayFilter {


    private PsfHeadkeyProperties properties;

    private AuthService authService;

    private BlackListService blackListService;

    @Autowired
    public AuthFilter(final PsfHeadkeyProperties properties, final PsfHeadkeyProperties properties1, final AuthService authService, final BlackListService blackListService) {
        super(properties);
        this.properties = properties1;
        this.authService = authService;
        this.blackListService = blackListService;
    }

    @Override
    public void beforeProcess(final ServerWebExchange exchange) {
    }

    @Override
    protected Mono<Void> afterProcess(final ServerWebExchange exchange, final GatewayFilterChain chain) {
        final boolean isAuth = authService.isAuth(exchange);
        final ServerHttpResponse response = exchange.getResponse();
        if (!isAuth) {
            return this.getresponse(response, "用户未登录");
        }
        final String usercode = authService.getUsercode(exchange);
        final boolean isBlackUser = blackListService.isBlackUser(usercode);
        if (isBlackUser) {
            return this.getresponse(response, "账号被封禁");
        }
        final String ip = this.getIp(exchange);
        final boolean isBlackIp = blackListService.isBlackIp(ip);
        if (isBlackIp) {
            return this.getresponse(response, "设备被封禁");
        }
        return chain.filter(exchange);
    }

    private Mono<Void> getresponse(final ServerHttpResponse response, final String errorMsg) {
        final ResultBean<Object> error = ResultBean.error(errorMsg);
        response.getHeaders().add("Content-Type", "application/json;charset=utf-8");
        final byte[] bytes = JSON.toJSONString(error).getBytes(Charset.forName("utf-8"));
        final DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }

    private String getIp(final ServerWebExchange serverWebExchange) {
        final ServerHttpRequest request = serverWebExchange.getRequest();
        final HttpHeaders headers = request.getHeaders();
        String ip = headers.getFirst("x-forwarded-for");
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            if (ip.indexOf(",") != -1) {
                ip = ip.split(",")[0];
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddress().getAddress().getHostAddress();
        }

        return ip.replaceAll(":", ".");
    }
}
