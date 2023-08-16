package com.muggle.psf.gateway.service;

/**
 * Description 黑名单接口定义
 * Date 2023/6/3
 * Created by muggle
 */
public interface BlackListService {
    boolean isBlackUser(String usercode);

    boolean isBlackIp(String ip);
}
