package org.example.ciphers;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.NoSuchAlgorithmException;

public interface CryptoAlgorithm {
    String getName();

    int getKeySize();

    int getIvSize();

    SecretKey generateKey() throws NoSuchAlgorithmException;

    IvParameterSpec generateIv() throws NoSuchAlgorithmException;

    Cipher getCipher(String mode) throws NoSuchPaddingException, NoSuchAlgorithmException;

    SecretKey keyFromBytes(byte[] keyBytes);

    IvParameterSpec ivFromBytes(byte[] ivBytes);
}