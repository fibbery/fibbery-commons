package com.fibbery.commons.algorithm.hash;

import java.security.MessageDigest;

/**
 * Created by jiangnenghua on 17/11/7.   desc :
 */
public enum  HashAlgorithm {
    // hash一致性算法
    KETAMA_HASH;

    public byte[] md5(String key) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.update(key.getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
