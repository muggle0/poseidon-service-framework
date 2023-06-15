package com.muggle.psf.gateway.config;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSONObject;
import com.muggle.psf.common.exception.GatewayException;
import com.muggle.psf.common.result.CommonErrorCode;
import com.muggle.psf.common.result.ResultBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.Map;

/**
 * Description
 * Date 2023/6/6
 * Created by muggle
 */
@Slf4j
public class PsfErrorWebExceptionHandler extends DefaultErrorWebExceptionHandler {

    private final MessageSource messageSource;

    public PsfErrorWebExceptionHandler(final ErrorAttributes errorAttributes, final ResourceProperties resourceProperties, final ErrorProperties errorProperties, final ApplicationContext applicationContext, final MessageSource messageSource) {
        super(errorAttributes, resourceProperties, errorProperties, applicationContext);
        this.messageSource = messageSource;
    }

    /**
     * 获取异常属性
     */
    @Override
    protected Map<String, Object> getErrorAttributes(final ServerRequest request, final boolean includeStackTrace) {
        int code = 500;
        final Throwable error = super.getError(request);
        if (error instanceof org.springframework.cloud.gateway.support.NotFoundException) {
            code = 404;
        }
        return this.buildMessage(code, request, error);
    }

    /**
     * 指定响应处理方法为JSON处理的方法
     *
     * @param errorAttributes
     */
    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(final ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }


    @Override
    protected HttpStatus getHttpStatus(final Map<String, Object> errorAttributes) {
        final int statusCode = (int) errorAttributes.get("code");
        if (statusCode == 404 || statusCode == 502) {
            return HttpStatus.valueOf(statusCode);
        }
        return HttpStatus.valueOf(500);
    }

    /**
     * 构建异常信息
     *
     * @param request
     * @param ex
     * @return
     */
    private Map<String, Object> buildMessage(final int code, final ServerRequest request, final Throwable ex) {
        final ResultBean<Object> error = ResultBean.error(ex.getMessage(), code);
        if (ex instanceof BlockException) {
            error.setCode(CommonErrorCode.LIMIT.code);
        }
        if (ex instanceof GatewayException) {
            error.setCode(CommonErrorCode.GATEWAY.code);
        }
        final String message = messageSource.getMessage(error.getCode().toString(), null, ex.getMessage(), LocaleContextHolder.getLocale());
        error.setMessage(message);
        log.error("gateway error", ex);
        return JSONObject.parseObject(JSONObject.toJSONString(error), Map.class);
    }

}
