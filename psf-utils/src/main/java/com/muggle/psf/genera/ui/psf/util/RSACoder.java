package com.muggle.psf.genera.ui.psf.util;


/**
 * @program: security-test
 * @description: 非对称加密算法
 * @author: muggle
 * @create: 2019-04-20
 **/


import javax.crypto.Cipher;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * 非对称加密算法RSA算法组件
 * 非对称算法一般是用来传送对称加密算法的密钥来使用的，相对于DH算法，RSA算法只需要一方构造密钥，不需要
 * 大费周章的构造各自本地的密钥对了。DH算法只能算法非对称算法的底层实现。而RSA算法算法实现起来较为简单
 *
 * @author kongqz
 */
public class RSACoder {
    //非对称密钥算法
    public static final String KEY_ALGORITHM = "RSA";


    /**
     * 密钥长度，DH算法的默认密钥长度是1024
     * 密钥长度必须是64的倍数，在512到65536位之间
     */
    private static final int KEY_SIZE = 512;
    //公钥
    private static final String PUBLIC_KEY = "RSAPublicKey";

    //私钥
    private static final String PRIVATE_KEY = "RSAPrivateKey";

    /**
     * 初始化密钥对
     *
     * @return Map 甲方密钥的Map
     */
    public static Map<String, Object> initKey() throws Exception {
        //实例化密钥生成器
        final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        //初始化密钥生成器
        keyPairGenerator.initialize(KEY_SIZE);
        //生成密钥对
        final KeyPair keyPair = keyPairGenerator.generateKeyPair();
        //甲方公钥
        final RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        //甲方私钥
        final RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        //将密钥存储在map中
        final Map<String, Object> keyMap = new HashMap<String, Object>();
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;

    }


    /**
     * 私钥加密
     *
     * @param data 待加密数据
     * @param key  密钥
     * @return byte[] 加密数据
     */
    public static byte[] encryptByPrivateKey(final byte[] data, final byte[] key) throws Exception {

        //取得私钥
        final PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(key);
        final KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        //生成私钥
        final PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        //数据加密
        final Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    /**
     * 公钥加密
     *
     * @param data 待加密数据
     * @param key  密钥
     * @return byte[] 加密数据
     */
    public static byte[] encryptByPublicKey(final byte[] data, final byte[] key) throws Exception {

        //实例化密钥工厂
        final KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        //初始化公钥
        //密钥材料转换
        final X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);
        //产生公钥
        final PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);

        //数据加密
        final Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        return cipher.doFinal(data);
    }

    /**
     * 私钥解密
     *
     * @param data 待解密数据
     * @param key  密钥
     * @return byte[] 解密数据
     */
    public static byte[] decryptByPrivateKey(final byte[] data, final byte[] key) throws Exception {
        //取得私钥
        final PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(key);
        final KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        //生成私钥
        final PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        //数据解密
        final Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    /**
     * 公钥解密
     *
     * @param data 待解密数据
     * @param key  密钥
     * @return byte[] 解密数据
     */
    public static byte[] decryptByPublicKey(final byte[] data, final byte[] key) throws Exception {

        //实例化密钥工厂
        final KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        //初始化公钥
        //密钥材料转换
        final X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);
        //产生公钥
        final PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
        //数据解密
        final Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, pubKey);
        return cipher.doFinal(data);
    }

    /**
     * 取得私钥
     *
     * @param keyMap 密钥map
     * @return byte[] 私钥
     */
    public static byte[] getPrivateKey(final Map<String, Object> keyMap) {
        final Key key = (Key) keyMap.get(PRIVATE_KEY);
        return key.getEncoded();
    }

    /**
     * 取得公钥
     *
     * @param keyMap 密钥map
     * @return byte[] 公钥
     */
    public static byte[] getPublicKey(final Map<String, Object> keyMap) throws Exception {
        final Key key = (Key) keyMap.get(PUBLIC_KEY);
        return key.getEncoded();
    }

    public static void main(final String[] args) throws Exception {
        //初始化密钥
        //生成密钥对
        final Map<String, Object> keyMap = RSACoder.initKey();
        //公钥
        final byte[] publicKey = RSACoder.getPublicKey(keyMap);

        //私钥
        final byte[] privateKey = RSACoder.getPrivateKey(keyMap);

        final String str = "RSA密码交换算法";

        //甲方进行数据的加密
        final byte[] code1 = RSACoder.encryptByPrivateKey(str.getBytes(), privateKey);

        //乙方进行数据的解密
        final byte[] decode1 = RSACoder.decryptByPublicKey(code1, publicKey);

        final Map<String, Object> ss = RSACoder.initKey();

        //公钥
        final byte[] a = RSACoder.getPublicKey(keyMap);

        //私钥
        final byte[] b = RSACoder.getPrivateKey(keyMap);

        final byte[] dexx = RSACoder.decryptByPublicKey(code1, a);

        System.out.println(new String(dexx));

       /* str = "乙方向甲方发送数据RSA算法";

        //乙方使用公钥对数据进行加密
        byte[] code2 = RSACoder.encryptByPublicKey(str.getBytes(), publicKey);

        //甲方使用私钥对数据进行解密
        byte[] decode2 = RSACoder.decryptByPrivateKey(code2, privateKey);

        System.out.println("甲方解密后的数据：" + new String(decode2));*/
    }

}