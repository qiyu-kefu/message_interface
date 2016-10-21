package com.qiyukf.openapi.session.util;

import java.security.MessageDigest;

/**
 * Created by zhoujianghua on 2016/4/14.
 */
public class QiyuPushCheckSum {

    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String encode(String appSecret, String nonce, long time) {
        String content = appSecret + nonce + time;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("sha1");
            messageDigest.update(content.getBytes());
            return getFormattedText(messageDigest.digest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String getFormattedText(byte[] bytes) {
        int len = bytes.length;
        StringBuilder buf = new StringBuilder(len * 2);
        for (int j = 0; j < len; j++) {
            buf.append(HEX_DIGITS[(bytes[j] >> 4) & 0x0f]);
            buf.append(HEX_DIGITS[bytes[j] & 0x0f]);
        }
        return buf.toString();
    }

    public static void main(String[] args) {
        String appSecret = "DD0F1F5A3AD04EF49A06547C253F9357";
        String body = "{\"uid\":\"ob45dwaR1oVceP-AzkIMIfpYgXgE\",\"staffId\":17885,\"sessionId\":815518,\"staffName\":\"超级管理员\",\"staffType\":1,\"code\":200}";
        String md5 = MD5.md5(body);
        long time = 1463725840;

        System.out.println(encode(appSecret, md5, time));
    }
}
