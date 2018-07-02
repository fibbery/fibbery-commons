package com.fibbery.commons.algorithm.hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author fibbery
 * @date 18/6/21
 */
public enum HashAlgorithm {
    NATIVE_HASH,
    CRC_HASH,
    KETAMA_HASH;

    private static MessageDigest md5Digest = null;

    static {
        try {
            md5Digest = MessageDigest.getInstance("md5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
