package com.muggle.psf.gateway.config;

import com.alibaba.fastjson.JSONObject;
import com.muggle.psf.common.result.ResultBean;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
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
public class PsfErrorWebExceptionHandler extends DefaultErrorWebExceptionHandler {
    public PsfErrorWebExceptionHandler(final ErrorAttributes errorAttributes, final ResourceProperties resourceProperties, final ErrorProperties errorProperties, final ApplicationContext applicationContext) {
        super(errorAttributes, resourceProperties, errorProperties, applicationContext);
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

    /**
     * 根据code获取对应的HttpStatus
     *
     * @param errorAttributes
     */
    @Override
    protected HttpStatus getHttpStatus(final Map<String, Object> errorAttributes) {
        final int statusCode = (int) errorAttributes.get("code");
        return HttpStatus.valueOf(statusCode);
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
        return JSONObject.parseObject(JSONObject.toJSONString(error), Map.class);
    }

}
