package com.muggle.psf.util;

import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.AES;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;

/**
 * Description
 * Date 2023/6/14
 * Created by muggle
 */
public class SecretKeyUtils {
    private final static String keypading = "1234567890987651";


    public static String encryptFromString(final String data, String key) {
        if (StringUtils.isEmpty(key)) {
            return data;
        }
        if (key.length() < 16) {
            key = key.concat(keypading);
        }
        final AES aes = new AES(Mode.ECB, Padding.PKCS5Padding, key.substring(0, 16).getBytes(StandardCharsets.UTF_8));
        // 加密并进行Base转码
        final String encrypt = aes.encryptBase64(data);
        return encrypt;
    }

    /**
     * ASE 解密
     * @param data
     * @param key
     * @return
     */
    public static String decryptFromString(final String data, String key) {
        if (StringUtils.isEmpty(key)) {
            return data;
        }
        if (key.length() < 16) {
            key = key.concat(keypading);
        }
        final AES aes = new AES(Mode.ECB, Padding.PKCS5Padding, key.substring(0, 16).getBytes(StandardCharsets.UTF_8));
        final String decryptStr = aes.decryptStr(data);
        return decryptStr;
    }
}
