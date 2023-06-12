package com.muggle.psf.gateway.repository;

import org.springframework.stereotype.Component;

/**
 * Description
 * Date 2023/6/8
 * Created by muggle
 */
@Component
public class AuthUserRepository {

    private final static ThreadLocal<String> caches = new ThreadLocal();

    public String getUserCode() {
        return caches.get();
    }

    public void deleteUserCode() {
        caches.remove();
    }

    public void saveUserCode(String userCode) {
        caches.set(userCode);
    }
    

}
