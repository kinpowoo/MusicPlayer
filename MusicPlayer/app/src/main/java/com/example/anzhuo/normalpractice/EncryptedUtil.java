package com.example.anzhuo.normalpractice;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by joker on 2017/1/19.
 */

public class EncryptedUtil {
    public static String encrypted(String id) {
        try {
            byte[] bytes1 = "3go8&$8*3*3h0k(2)2".getBytes();
            byte[] bytes2 = id.getBytes();
            int byte1Length = bytes1.length;
            for (int i = 0; i < bytes2.length; i++) {
                bytes2[i] = (byte) (bytes2[i] ^ bytes1[i % byte1Length]);
            }
            MessageDigest md5 = null;
            md5 = MessageDigest.getInstance("MD5");
            md5.update(bytes2);
            Base64Encoder base64en = new Base64Encoder();
            String substring = base64en.encode(md5.digest());

            substring = substring.substring(0, substring.length());
            substring = substring.replace('/', '_');
            substring = substring.replace('+', '-');
            return substring;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return "";
    }
}