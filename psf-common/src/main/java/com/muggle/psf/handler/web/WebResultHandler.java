package com.muggle.psf.handler.web;


import com.muggle.psf.event.ExceptionEvent;
import com.muggle.psf.exception.BasePoseidonCheckException;
import com.muggle.psf.exception.BasePoseidonException;
import com.muggle.psf.result.ResultBean;
import com.muggle.psf.base.UserInfo;
import com.muggle.psf.tool.UserInfoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;


import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class WebResultHandler {

    public WebResultHandler() {
        log.debug(">>>>>>>>>>>>>>>>>>>>> WebResultHandler 注册 <<<<<<<<<<<<<<<<<<<<");
    }

    private static final Logger log = LoggerFactory.getLogger(WebResultHandler.class);

    @Autowired
    ApplicationContext applicationContext;


    /**
     * 自定义异常
     *
     * @param e
     * @param req
     * @return
     */
    @ExceptionHandler(value = {BasePoseidonException.class})
    public ResultBean poseidonExceptionHandler(BasePoseidonException e, HttpServletRequest req) {
        log.error("业务异常,错误码：{}", e.getCode(), e);
        ResultBean error = ResultBean.error(e.getMessage(), e.getCode() == null ? 5001 : e.getCode());
        return error;
    }

    /**
     * 参数未通过校验
     *
     * @param e
     * @param req
     * @return
     */
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResultBean methodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest req) {
        log.error("参数未通过校验", e);
        ResultBean error = ResultBean.error(e.getBindingResult().getFieldError().getDefaultMessage());
        return error;
    }

    /**
     * BeanPropertyBindingResult
     */
    @ExceptionHandler(value = {BindException.class})
    public ResultBean beanPropertyBindingResult(BindException e, HttpServletRequest req) {
        log.error("参数未通过校验", e);
        ResultBean error = ResultBean.error(e.getFieldError().getDefaultMessage());
        return error;
    }

    /**
     * 错误的请求方式
     *
     * @param e
     * @param req
     * @return
     */
    @ExceptionHandler(value = {HttpRequestMethodNotSupportedException.class})
    public ResultBean notsupported(Exception e, HttpServletRequest req) {
        log.error("错误的请求方式", e);
        ResultBean error = ResultBean.error("错误的请求方式");
        return error;
    }

    /**
     * 请求路径不存在
     *
     * @param e
     * @param req
     * @return
     */
    @ExceptionHandler(value = {NoHandlerFoundException.class})
    public ResultBean notFoundUrl(Exception e, HttpServletRequest req) {
        log.error("请求路径不存在", e);
        ResultBean error = ResultBean.error("请求路径不存在");
        return error;
    }


    /**
     * 自定义异常
     *
     * @param e
     * @param req
     * @return
     */
    @ExceptionHandler(value = {BasePoseidonCheckException.class})
    public ResultBean checked(BasePoseidonCheckException e, HttpServletRequest req) {
        log.error("自定义异常，错误码:{}", e.getCode(), e);
        ResultBean error = ResultBean.error(e.getMessage(), e.getCode() == null ? 5001 : e.getCode());
        return error;
    }

    /**
     * 未知异常，需要通知到管理员,对于线上未知的异常，我们应该严肃处理：先将消息传给MQ中心(该平台未实现) 然后日志写库
     * 这里的处理方式是抛出事件
     *
     * @param e
     * @param req
     * @return
     */
    @ExceptionHandler(value = {Exception.class})
    public ResultBean exceptionHandler(Exception e, HttpServletRequest req) {
        try {
            UserInfo userInfo = UserInfoUtils.getUserInfo();
            log.error("系统异常：" + req.getMethod() + req.getRequestURI() + " user: " + (userInfo == null ? "无用户信息" : userInfo.toString()), e);
            ExceptionEvent exceptionEvent = new ExceptionEvent(String.format("系统异常: [ %s ] 时间戳： [%d]  ", e.getMessage(), System.currentTimeMillis()), this);
            applicationContext.publishEvent(exceptionEvent);
            return ResultBean.error("系统异常", 500);
        } catch (Exception err) {
            log.error("紧急！！！ 严重的异常", err);
            return ResultBean.error("系统发生严重的错误", 500);
        }
    }
}
