package com.hieptran.smarthome_server.utils;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;
import org.springframework.beans.factory.annotation.Value;

import java.util.Random;

public class EncryptionUtils {
    @Value("${encrypt.secret}")
    private static String SECRET_KEY;

    private static final String ALGORITHM = "PBEWITHHMACSHA512ANDAES_256";

    private static StandardPBEStringEncryptor encryptor;

    static {
        encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(SECRET_KEY);
        encryptor.setAlgorithm(ALGORITHM);
        encryptor.setIvGenerator(new RandomIvGenerator());
        encryptor.initialize();
    }

    public static String encrypt(String data) {
        return encryptor.encrypt(data);
    }

    public static String decrypt(String encryptedData) {
        return encryptor.decrypt(encryptedData);
    }

    public static String generateVerificationCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }
}
