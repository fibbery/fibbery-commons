package com.fibbery.commons.utils;

import org.apache.commons.lang3.StringUtils;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author fibbery
 * @date 17/12/22
 */
public class Md5Utils {

    private static Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    public static String md5(String message) {
        StringBuilder encryptTxt = new StringBuilder(StringUtils.EMPTY);
        if (StringUtils.isEmpty(message)) {
            return encryptTxt.toString();
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("md5");
            digest.update(message.getBytes(DEFAULT_CHARSET));
            byte[] values = digest.digest();
            for (byte value : values) {
                int temp = value & 255;
                if (temp < 16) {
                    encryptTxt.append("0").append(Integer.toHexString(temp));
                } else {
                    encryptTxt.append(Integer.toHexString(temp));
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return encryptTxt.toString();
    }

}
