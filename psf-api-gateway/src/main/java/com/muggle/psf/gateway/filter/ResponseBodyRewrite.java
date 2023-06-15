package com.muggle.psf.gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.muggle.psf.common.exception.GatewayException;
import com.muggle.psf.common.result.ErrorCode;
import com.muggle.psf.common.result.ResultBean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.context.MessageSource;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Description
 * Date 2023/6/13
 * Created by muggle
 */
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class ResponseBodyRewrite implements RewriteFunction<String, String> {

    private final ObjectMapper objectMapper;

    private final MessageSource messageSource;


    @Override
    public Publisher<String> apply(final ServerWebExchange exchange, final String body) {
        try {
            final ResultBean resultBean = objectMapper.readValue(body, ResultBean.class);
            final Integer code = resultBean.getCode();
            final String message = messageSource.getMessage(String.valueOf(code),
                null, ErrorCode.SYSTEM, exchange.getLocaleContext().getLocale());
            resultBean.setMessage(message);
            return Mono.just(objectMapper.writeValueAsString(resultBean));
        } catch (final Exception ex) {
            log.error("2. json process fail", ex);
            return Mono.error(new GatewayException("json process fail"));
        }
    }
}
