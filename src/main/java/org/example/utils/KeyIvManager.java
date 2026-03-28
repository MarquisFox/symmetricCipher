package org.example.utils;

import org.example.ciphers.CryptoAlgorithm;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class KeyIvManager {
    private CryptoAlgorithm algorithm;
    private SecretKey secretKey;
    private IvParameterSpec ivSpec;

    public KeyIvManager(CryptoAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public void generateNewKeyAndIv() {
        try {
            secretKey = algorithm.generateKey();
            ivSpec = algorithm.generateIv();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Ошибка генерации ключа: " + e.getMessage());
        }
    }

    public void saveKeyToFile(String keyFile) throws IOException {
        if (secretKey == null) throw new IllegalStateException("Ключ не сгенерирован");
        try (FileOutputStream fos = new FileOutputStream(keyFile)) {
            fos.write(secretKey.getEncoded());
        }
    }

    public void saveIvToFile(String ivFile) throws IOException {
        if (ivSpec == null) throw new IllegalStateException("IV не сгенерирован");
        try (FileOutputStream fos = new FileOutputStream(ivFile)) {
            fos.write(ivSpec.getIV());
        }
    }

    public void loadKeyFromFile(String keyFile) throws IOException {
        byte[] keyBytes = loadBytes(keyFile);
        secretKey = algorithm.keyFromBytes(keyBytes);
    }

    public void loadIvFromFile(String ivFile) throws IOException {
        byte[] ivBytes = loadBytes(ivFile);
        ivSpec = algorithm.ivFromBytes(ivBytes);
    }

    private byte[] loadBytes(String file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            return fis.readAllBytes();
        }
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

    public IvParameterSpec getIvSpec() {
        return ivSpec;
    }

    public boolean isKeyLoaded() {
        return secretKey != null;
    }

    public boolean isIvLoaded() {
        return ivSpec != null;
    }
}
