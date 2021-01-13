package com.tom;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public abstract class HashUtils {

    public static String doHash(byte[] bytes, String previousHash) {
        byte[] encodedhash = new byte[0];
        String originalString = Arrays.toString(bytes) + "_" +
                previousHash;
        return getHex(encodedhash, originalString);
    }

    static String getHex(byte[] encodedhash, String originalString) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            encodedhash = digest.digest(
                    originalString.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return bytesToHex(encodedhash);
    }

    static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte aHash : hash) {
            String hex = Integer.toHexString(0xff & aHash);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
