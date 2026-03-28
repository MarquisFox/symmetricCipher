package org.example.ui;

import org.example.ciphers.AESAlgorithm;
import org.example.ciphers.CryptoAlgorithm;
import org.example.ciphers.DESAlgorithm;
import org.example.ciphers.TripleDESAlgorithm;
import org.example.services.CryptoService;
import org.example.utils.InputUtils;
import org.example.utils.KeyIvManager;

import java.io.IOException;
import java.util.Scanner;

public class ConsoleUI {
    private final Scanner scanner;
    private final InputUtils input;
    private CryptoAlgorithm algorithm;
    private KeyIvManager keyManager;
    private CryptoService cryptoService;

    public ConsoleUI() {
        this.scanner = new Scanner(System.in);
        this.input = new InputUtils(scanner);
        selectAlgorithm();
    }

    private void selectAlgorithm() {
        System.out.println("Выберите алгоритм:\n1. AES\n2. DES\n3. TripleDES");
        int choice = input.readInt("Введите номер: ");
        switch (choice) {
            case 1:
                algorithm = new AESAlgorithm();
                break;
            case 2:
                algorithm = new DESAlgorithm();
                break;
            case 3:
                algorithm = new TripleDESAlgorithm();
                break;
            default:
                algorithm = new AESAlgorithm();
                System.out.println("Используем AES.");
        }
        keyManager = new KeyIvManager(algorithm);
        cryptoService = new CryptoService(algorithm, keyManager);
        System.out.println("Выбран алгоритм: " + algorithm.getName());
    }

    public void run() {
        boolean exit = false;
        while (!exit) {
            printMenu();
            int choice = input.readInt("Выберите действие: ");
            switch (choice) {
                case 1:
                    generateKeyAndIv();
                    break;
                case 2:
                    loadKeyAndIv();
                    break;
                case 3:
                    processText(true);
                    break;
                case 4:
                    processText(false);
                    break;
                case 5:
                    processFile(true);
                    break;
                case 6:
                    processFile(false);
                    break;
                case 7:
                    exit = true;
                    System.out.println("Выход.");
                    break;
                default:
                    System.out.println("неверное значение");
            }
        }
        scanner.close();
    }

    private void printMenu() {
        StringBuilder sb = new StringBuilder();
        sb.append("Симметричное шифрование (").append(algorithm.getName()).append(")\n")
                .append("1. Сгенерировать ключ и IV\n2. Загрузить ключ и IV\n3. Зашифровать текст\n")
                .append("4. Расшифровать текст\n5. Зашифровать файл\n6. Расшифровать файл\n7. Выход\n");
        System.out.print(sb);
    }

    private void generateKeyAndIv() {
        keyManager.generateNewKeyAndIv();
        try {
            keyManager.saveKeyToFile("secret.key");
            keyManager.saveIvToFile("iv.iv");
            System.out.println("Ключ и IV сохранены в secret.key, iv.iv");
        } catch (IOException e) {
            System.err.println("Ошибка сохранения: " + e.getMessage());
        }
    }

    private void loadKeyAndIv() {
        String keyFile = input.readString("Файл ключа: ");
        String ivFile = input.readString("Файл IV: ");
        try {
            keyManager.loadKeyFromFile(keyFile);
            keyManager.loadIvFromFile(ivFile);
            System.out.println("Ключ и IV загружены.");
        } catch (IOException e) {
            System.err.println("Ошибка загрузки: " + e.getMessage());
        }
    }

    private boolean checkPreconditions(String mode) {
        if (!keyManager.isKeyLoaded()) {
            System.out.println("Ключ не задан.");
            return false;
        }
        if (!mode.equalsIgnoreCase("ECB") && !mode.equalsIgnoreCase("CBC")) {
            System.out.println("Поддерживаются только ECB и CBC.");
            return false;
        }
        if (mode.equalsIgnoreCase("CBC") && !keyManager.isIvLoaded()) {
            System.out.println("Для CBC требуется IV.");
            return false;
        }
        return true;
    }

    private void processText(boolean encrypt) {
        String mode = input.readString("Режим (ECB/CBC): ");
        if (!checkPreconditions(mode)) return;
        String data = input.readString(encrypt ? "Текст: " : "Base64: ");
        try {
            String result = encrypt ? cryptoService.encrypt(data, mode) : cryptoService.decrypt(data, mode);
            System.out.println(encrypt ? "Зашифровано (Base64): " + result : "Расшифровано: " + result);
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }


    private void processFile(boolean encrypt) {
        String mode = input.readString("Режим (ECB/CBC): ");
        if (!checkPreconditions(mode)) return;
        String inputFile = input.readString("Входной файл: ");
        String outputFile = input.readString("Выходной файл: ");
        try {
            if (encrypt) cryptoService.encryptFile(inputFile, outputFile, mode);
            else cryptoService.decryptFile(inputFile, outputFile, mode);
            System.out.println("Файл " + (encrypt ? "зашифрован" : "расшифрован") + ". Результат: " + outputFile);
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
        }

    }
}