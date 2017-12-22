package com.fibbery.commons.algorithm.boom;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;

/**
 * @author fibbery
 * @date 17/12/22
 */
public class BoomFilter {

    public static final int NUM_SLOTS = 1024 * 1024 * 8;

    public static final int NUM_HASH = 8;

    private BigInteger bitmap = new BigInteger("0");

    public void addElement(String message) {
        for (int index = 0; index < NUM_HASH; index++) {
            int hashcode = getHash(message, index);
            if (!bitmap.testBit(hashcode)) {
                bitmap = bitmap.or(new BigInteger("1").shiftLeft(hashcode));
            }
        }
    }

    private int getHash(String message, int index) {
        try {
            MessageDigest digist = MessageDigest.getInstance("md5");
            String newMessage = message + String.valueOf(index);
            digist.update(newMessage.getBytes(Charset.forName("UTF-8")));
            BigInteger num = new BigInteger(digist.digest());
            return Math.abs(num.intValue()) & NUM_SLOTS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean check(String message) {
        for (int index = 0; index < NUM_HASH; index++) {
            int hashcode = getHash(message, index);
            if (!bitmap.testBit(hashcode)) {
                return false;
            }
        }
        return true;
    }


    public static void main(String[] args) {
        BoomFilter boomFilter = new BoomFilter();
        boomFilter.addElement("hello zhoujielun");
        System.out.println(boomFilter.check("hello zhoujielun"));
    }
}
