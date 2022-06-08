package jansen.tom.rps.account.hashing;

import jansen.tom.rps.account.Account;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TokenHashing {

    private static byte[] digest(byte[] input) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA3-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
        return md.digest(input);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    static public String tokenHash(Account account) {
        // Create email and salt bytes
        byte[] salt = account.getHash();
        byte[] email = account.getEmail().getBytes(StandardCharsets.UTF_8);
        byte[] byteHash = ByteBuffer.allocate(salt.length + email.length).put(salt).put(email).array();
        return bytesToHex(digest(email));
    }

}
