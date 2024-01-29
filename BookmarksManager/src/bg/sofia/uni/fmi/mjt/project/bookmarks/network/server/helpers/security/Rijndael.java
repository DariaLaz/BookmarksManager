package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.helpers.security;

import bg.sofia.uni.fmi.mjt.project.bookmarks.exceptions.CipherException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.util.Arrays;

public class Rijndael implements SymmetricBlockCipher {
    private final SecretKey secretKey;
    private static final String ALGORITHM = "AES";
    public Rijndael(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public String encrypt(String input) throws CipherException {
        try {
            var cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Arrays.toString((cipher.doFinal(input.getBytes())));
        } catch (Exception e) {
            throw new CipherException("encrypting error");
        }
    }

    @Override
    public String decrypt(String inputStream) throws CipherException {
        try {
            var cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return Arrays.toString((cipher.doFinal(inputStream.getBytes())));
        } catch (Exception e) {
            throw new CipherException("decrypting error");
        }
    }

}
