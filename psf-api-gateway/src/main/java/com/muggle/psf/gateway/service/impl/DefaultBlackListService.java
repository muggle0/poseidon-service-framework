package com.muggle.psf.gateway.service.impl;

import com.muggle.psf.gateway.service.BlackListService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Description 黑名单服务
 * Date 2023/6/3
 * Created by muggle
 */
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class DefaultBlackListService implements BlackListService {


    @Override
    public boolean isBlackUser(final String usercode) {
        return false;
    }

    @Override
    public boolean isBlackIp(final String ip) {
        return false;
    }
}
