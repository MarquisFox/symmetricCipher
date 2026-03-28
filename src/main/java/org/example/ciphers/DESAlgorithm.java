package org.example.ciphers;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class DESAlgorithm implements CryptoAlgorithm {
    private static final String ALGORITHM = "DES";
    private static final int KEY_SIZE = 56;
    private static final int IV_SIZE = 8;

    @Override
    public String getName() {
        return "DES";
    }

    @Override
    public int getKeySize() {
        return KEY_SIZE;
    }

    @Override
    public int getIvSize() {
        return IV_SIZE;
    }

    @Override
    public SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(KEY_SIZE);
        return keyGen.generateKey();
    }

    @Override
    public IvParameterSpec generateIv() {
        byte[] iv = new byte[IV_SIZE];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    @Override
    public Cipher getCipher(String mode) throws NoSuchPaddingException, NoSuchAlgorithmException {
        String transformation = ALGORITHM + "/" + mode + "/PKCS5Padding";
        return Cipher.getInstance(transformation);
    }

    @Override
    public SecretKey keyFromBytes(byte[] keyBytes) {
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }

    @Override
    public IvParameterSpec ivFromBytes(byte[] ivBytes) {
        return new IvParameterSpec(ivBytes);
    }
}
