package jansen.tom.rps.account.hashing;

public class TokenHash {

    static public String getHash(String email, String salt) {

        System.out.println(email);
        System.out.println(salt);
        return email+salt;

    }

}
