package com.muggle.psf.common.result;


import java.io.Serializable;

/**
 * @program: poseidon-cloud-starter
 * @description:
 * @author: muggle
 * @create: 2019-11-05
 **/

public class ResultBean<T> implements Serializable {

    private static final long serialVersionUID = 1553485005784190508L;
    private String message;
    private Integer code;
    private T data;
    private Long total;

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(final Integer code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(final T data) {
        this.data = data;
    }

    private ResultBean(final String message, final Integer code, final T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    private ResultBean(final String message, final Integer code) {
        this.code = code;
        this.message = message;
    }


    public static <T> ResultBean<T> getInstance(final String message, final Integer code, final T data) {
        return new ResultBean<>(message, code, data);
    }

    public static <T> ResultBean<T> getInstance(final String message, final Integer code) {
        return new ResultBean<>(message, code);
    }

    public static <T> ResultBean<T> success() {
        return new ResultBean<>("请求成功", 200);
    }

    public static <T> ResultBean<T> success(final String message) {
        return new ResultBean<>(message, 200);
    }

    public static <T> ResultBean<T> error(final String message) {
        return new ResultBean<>(message, 5001);
    }

    public static <T> ResultBean<T> error(final String message, final Integer code) {
        return new ResultBean<>(message, code);
    }

    public static <T> ResultBean<T> successData(final T data) {
        return new ResultBean<>("请求成功", 200, data);
    }


    public ResultBean() {

    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(final Long total) {
        this.total = total;
    }
}
