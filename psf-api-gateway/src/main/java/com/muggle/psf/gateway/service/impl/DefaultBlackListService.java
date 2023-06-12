package com.muggle.psf.gateway.service.impl;

import com.muggle.psf.gateway.service.BlackListService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * Description 黑名单服务
 * Date 2023/6/3
 * Created by muggle
 */
@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@ConditionalOnMissingBean
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
