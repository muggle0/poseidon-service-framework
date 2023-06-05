package com.muggle.psf.genera.ui.psf.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Description:
 * @Author: muggle
 * @Date: 2020/8/28
 **/
public class SHA256Utils {

    public final static String SHA256 = "SHA-256";

    private SHA256Utils() {
        throw new UnsupportedOperationException();
    }

    public static String getSHA256StrJava(final String timeStamp, final String token, final String passId, final String collection) {
        return getSHA256StrJava(timeStamp + token + passId + collection);
    }

    public static String getSHA256StrJava(final String str) {
        String encodeStr = "";

        try {
            final MessageDigest messageDigest = MessageDigest.getInstance(SHA256);
            messageDigest.update(str.getBytes(StandardCharsets.UTF_8));
            encodeStr = byte2Hex(messageDigest.digest());
        } catch (final NoSuchAlgorithmException var4) {
            var4.printStackTrace();
        }

        return encodeStr;
    }

    public static String getSHA512Encrypt(final String str) {
        String encodeStr = "";

        try {
            final MessageDigest messageDigest = MessageDigest.getInstance(SHA256);
            messageDigest.update(str.getBytes(StandardCharsets.UTF_8));
            encodeStr = byte2Hex(messageDigest.digest());
        } catch (final NoSuchAlgorithmException var4) {
            var4.printStackTrace();
        }

        return encodeStr;
    }

    private static String byte2Hex(final byte[] bytes) {
        final StringBuffer stringBuffer = new StringBuffer();
        String temp = null;

        for (int i = 0; i < bytes.length; ++i) {
            temp = Integer.toHexString(bytes[i] & 255);
            if (temp.length() == 1) {
                stringBuffer.append("0");
            }

            stringBuffer.append(temp);
        }

        return stringBuffer.toString();
    }
}
