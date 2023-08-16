package com.muggle.psf.gateway.filter;

import com.muggle.psf.gateway.properties.PsfHeadkeyProperties;
import com.muggle.psf.gateway.service.AuthService;
import com.muggle.psf.gateway.service.BlackListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * Description
 * Date 2023/6/15
 * Created by muggle
 */
@Component
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class PsfAuthGatewayFilterFactory extends AbstractGatewayFilterFactory implements Ordered {

    private final PsfHeadkeyProperties psfHeadkeyProperties;

    private final AuthService authService;

    private final BlackListService blackListService;

    @Override
    public GatewayFilter apply(final Object config) {
        return new AuthFilter(psfHeadkeyProperties, authService, blackListService);
    }

    @Override
    public int getOrder() {
        return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER - 1;
    }
}
