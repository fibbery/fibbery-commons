package com.fibbery.commons.utils;

import javax.crypto.Cipher;
import java.nio.charset.Charset;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @author fibbery
 * @date 18/2/7
 */
public class RSAUtils {

    public static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048, new SecureRandom());
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取base64编码私钥字符串
     * @param pair
     * @return
     */
    public static String getPrivateKey(KeyPair pair) {
        PrivateKey privateKey = pair.getPrivate();
        return base64Encode(privateKey.getEncoded());
    }

    /**
     * 获取base64编码的公钥字符串
     * @param pair
     * @return
     */
    public static String getPublicKey(KeyPair pair) {
        PublicKey publicKey = pair.getPublic();
        return base64Encode(publicKey.getEncoded());
    }

    /**
     * 将 base64 加密的字符串实例化成私钥对象
     * @param privateKey
     * `@return
     */
    public static PrivateKey getPrivateKey(String privateKey) {
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(base64Decode(privateKey));
        try {
            KeyFactory keyFactory= KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(spec);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将base64 加密的字符串实例化成公钥对象
     * @param publicKey
     * @return
     */
    public static PublicKey getPublicKey(String publicKey) {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(base64Decode(publicKey));
        try {
            KeyFactory factory = KeyFactory.getInstance("RSA");
            return factory.generatePublic(spec);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 用私钥加密数据
     * @param privateKey
     * @param data
     * @return
     */
    public static String encryptPrivate(String privateKey, String data) {
        return encrypt(getPrivateKey(privateKey), data);
    }

    /**
     * 使用公钥解密
     * @param publicKey
     * @param data
     * @return
     */
    public static String decryptPublic(String publicKey, String data) {
        return decrypt(getPublicKey(publicKey), data);
    }

    public static String encryptPublic(String publicKey, String data) {
        return encrypt(getPublicKey(publicKey), data);
    }

    public static String decyptPrivate(String privateKey, String data) {
        return decrypt(getPrivateKey(privateKey), data);
    }

    public static String sign(String privateKey, String data) {
        try {
            Signature signature = Signature.getInstance("MD5withRSA");
            signature.initSign(getPrivateKey(privateKey));
            signature.update(data.getBytes(Charset.defaultCharset()));
            return base64Encode(signature.sign());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean verify(String publicKey, String signData, String originData) {
        try {
            Signature signature = Signature.getInstance("MD5withRSA");
            signature.initVerify(getPublicKey(publicKey));
            signature.update(originData.getBytes(Charset.defaultCharset()));
            return signature.verify(base64Decode(signData));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static String encrypt(Key key, String data) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] finalData = cipher.doFinal(data.getBytes(Charset.defaultCharset()));
            return base64Encode(finalData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String decrypt(Key key, String data) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] originData = base64Decode(data);
            return new String(cipher.doFinal(originData), Charset.defaultCharset());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String base64Encode(byte[] datas) {
        return Base64.getEncoder().encodeToString(datas);
    }

    private static byte[] base64Decode(String data) {
        return Base64.getDecoder().decode(data);
    }

    public static void main(String[] args) {
        KeyPair pair = generateKeyPair();
        String privateKey = getPrivateKey(pair);
        String publicKey = getPublicKey(pair);
        String data = "hello world";
        String encrypt = encryptPublic(publicKey, data);
        System.out.println("encrypt by private : " + encrypt);
        System.out.println("decrypt by private :" + decyptPrivate(privateKey, encrypt));
        String sign = sign(privateKey, data);
        System.out.println("sign data is : " + sign);
        System.out.println("verify by public key is : " + verify(publicKey, sign, data));
    }
}
