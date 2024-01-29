package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.helpers.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class PasswordHash implements Hash {
    private static final String HASHING_ALGORITHM = "SHA-256";
    @Override
    public String hash(String password) {
        try {
            var messageDigest = MessageDigest.getInstance(HASHING_ALGORITHM);
            byte[] hashBytes = messageDigest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean verify(String password, String hash) {
        return hash(password).equals(hash);
    }
}
