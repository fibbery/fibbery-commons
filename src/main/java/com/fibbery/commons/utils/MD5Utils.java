package com.fibbery.commons.utils;

import org.apache.commons.lang3.StringUtils;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author fibbery
 * @date 17/12/22
 */
public class MD5Utils {

    private static Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    public static String md5(String message) {
        String encryptTxt = StringUtils.EMPTY;
        if (StringUtils.isEmpty(message)) {
            return encryptTxt;
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("md5");
            digest.update(message.getBytes(DEFAULT_CHARSET));
            byte[] values = digest.digest();
            for (byte value : values) {
                int temp = value & 255;
                if (temp < 16) {
                    encryptTxt += "0" + Integer.toHexString(temp);
                } else {
                    encryptTxt += Integer.toHexString(temp);
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return encryptTxt;
    }

    public static void main(String[] args) {
        System.out.println(md5("test"));
    }
}
