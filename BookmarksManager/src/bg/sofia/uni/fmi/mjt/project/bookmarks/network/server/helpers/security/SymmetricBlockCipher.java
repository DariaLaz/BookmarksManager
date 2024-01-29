package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.helpers.security;

import bg.sofia.uni.fmi.mjt.project.bookmarks.exceptions.CipherException;

public interface SymmetricBlockCipher {
    /**
     * Encrypts the data from inputStream and puts it into outputStream
     *
     * @throws CipherException if the encrypt/decrypt operation cannot be completed successfully
     */
    String encrypt(String input) throws CipherException;

    /**
     * Decrypts the data from inputStream and puts it into outputStream
     *
     * @throws CipherException if the encrypt/decrypt operation cannot be completed successfully
     */
    String decrypt(String input) throws CipherException;
}

