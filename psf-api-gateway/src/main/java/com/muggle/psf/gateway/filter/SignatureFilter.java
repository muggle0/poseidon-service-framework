package com.muggle.psf.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.muggle.psf.common.exception.GatewayException;
import com.muggle.psf.common.result.ResultBean;
import com.muggle.psf.gateway.properties.PsfHeadkeyProperties;
import com.muggle.psf.gateway.signature.SignatureHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;

/**
 * Description
 * Date 2023/6/5
 * Created by muggle
 *
 * @author muggle
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "gateway", name = "api.signature.enabled", havingValue = "true", matchIfMissing = true)
@Order(NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER - 3)
public class SignatureFilter extends BaseGatewayFilter {

    private final PsfHeadkeyProperties properties;

    private final SignatureHandler signatureHandler;

    @Autowired
    public SignatureFilter(final PsfHeadkeyProperties properties, final SignatureHandler signatureHandler) {
        super(properties);
        this.properties = properties;
        this.signatureHandler = signatureHandler;
        log.info("SignatureFilter init 》》》》》");
    }


    @Override
    public void beforeProcess(final ServerWebExchange exchange) {

    }

    @Override
    protected Mono<Void> afterProcess(final ServerWebExchange exchange, final GatewayFilterChain chain) {
        try {
            final ServerHttpRequest request = exchange.getRequest();
            signatureHandler.checkSign(request, properties);
        } catch (final GatewayException e) {
            log.error("SignatureFilter error", e);
            final ResultBean<Object> error = ResultBean.error(e.getMessage(), e.getCode());
            final byte[] bytes = JSON.toJSONString(error).getBytes(Charset.forName("utf-8"));
            final ServerHttpResponse response = exchange.getResponse();
            response.getHeaders().add("Content-Type", "application/json;charset=utf-8");
            final DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        }
        return chain.filter(exchange);
    }

}
