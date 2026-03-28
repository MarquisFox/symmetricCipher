package org.example.services;

import org.example.ciphers.CryptoAlgorithm;
import org.example.utils.FileUtils;
import org.example.utils.KeyIvManager;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class CryptoService {
    private final CryptoAlgorithm algorithm;
    private final KeyIvManager keyManager;

    public CryptoService(CryptoAlgorithm algorithm, KeyIvManager keyManager) {
        this.algorithm = algorithm;
        this.keyManager = keyManager;
    }

    public String encrypt(String plaintext, String mode) throws Exception {
        if (!keyManager.isKeyLoaded()) throw new IllegalStateException("Ключ не задан");
        if (mode.equalsIgnoreCase("CBC") && !keyManager.isIvLoaded()) {
            throw new IllegalStateException("Для режима CBC требуется IV");
        }

        Cipher cipher = algorithm.getCipher(mode.toUpperCase());
        if (mode.equalsIgnoreCase("CBC")) {
            cipher.init(Cipher.ENCRYPT_MODE, keyManager.getSecretKey(), keyManager.getIvSpec());
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, keyManager.getSecretKey());
        }

        byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(ciphertext);
    }

    public String decrypt(String ciphertextBase64, String mode) throws Exception {
        if (!keyManager.isKeyLoaded()) throw new IllegalStateException("Ключ не задан");
        if (mode.equalsIgnoreCase("CBC") && !keyManager.isIvLoaded()) {
            throw new IllegalStateException("Для режима CBC требуется IV");
        }

        Cipher cipher = algorithm.getCipher(mode.toUpperCase());
        if (mode.equalsIgnoreCase("CBC")) {
            cipher.init(Cipher.DECRYPT_MODE, keyManager.getSecretKey(), keyManager.getIvSpec());
        } else {
            cipher.init(Cipher.DECRYPT_MODE, keyManager.getSecretKey());
        }

        byte[] ciphertext = Base64.getDecoder().decode(ciphertextBase64);
        byte[] plaintext = cipher.doFinal(ciphertext);
        return new String(plaintext, StandardCharsets.UTF_8);
    }

    public void encryptFile(String inputFile, String outputFile, String mode) throws Exception {
        if (!keyManager.isKeyLoaded()) {
            throw new IllegalStateException("Ключ не задан");
        }
        if (mode.equalsIgnoreCase("CBC") && !keyManager.isIvLoaded()) {
            throw new IllegalStateException("Для режима CBC требуется IV");
        }
        String plaintext = FileUtils.readTextFile(inputFile);
        String ciphertext = encrypt(plaintext, mode);
        FileUtils.writeTextFile(outputFile, ciphertext);
    }

    public void decryptFile(String inputFile, String outputFile, String mode) throws Exception {
        if (!keyManager.isKeyLoaded()) {
            throw new IllegalStateException("Ключ не задан");
        }
        if (mode.equalsIgnoreCase("CBC") && !keyManager.isIvLoaded()) {
            throw new IllegalStateException("Для режима CBC требуется IV");
        }
        String ciphertext = FileUtils.readTextFile(inputFile);
        String plaintext = decrypt(ciphertext, mode);
        FileUtils.writeTextFile(outputFile, plaintext);
    }


}
