package jansen.tom.rps.account.hashing;

import jansen.tom.rps.account.Account;
import jansen.tom.rps.authentication.Authentication;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TokenHashing {

    private final static Charset UTF_8 = StandardCharsets.UTF_8;

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

    static public String tokenHash(Account account, Authentication authentication) {
        // Email, salt plus time bytes
        byte[] salt = account.getHash();
        byte[] email = account.getEmail().getBytes(UTF_8);
        byte[] time = authentication.getCreationTime().toString().getBytes(UTF_8);
        int bufferLength = salt.length + email.length + time.length;
        // Before we can digest the token hash we must first add all the elements together in array
        byte[] byteHash = ByteBuffer.allocate(bufferLength).put(salt).put(email).put(time).array();
        return bytesToHex(digest(byteHash));
    }

}
