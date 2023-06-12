package com.muggle.psf.gateway.service;

import org.springframework.web.server.ServerWebExchange;

/**
 * Description
 * Date 2023/6/8
 * Created by muggle
 */

public interface AuthService {

    boolean isAuth(ServerWebExchange exchange);

    String getUsercode(final ServerWebExchange exchange);
}
