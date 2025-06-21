package utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class EncryptionUtil {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";

    // Encrypts plain text using AES
    public static String encrypt(String plainText, String secretKey) throws Exception {
        SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    // Decrypts cipher text using AES
    public static String decrypt(String cipherText, String secretKey) throws Exception {
        SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decoded = Base64.getDecoder().decode(cipherText);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted);
    }
  //encrypt String Key
    public static String encryptKey(String key) {
        try {
            return encrypt(key, "1234567890123456"); // Example secret key, should be securely stored
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    // Decrypt String Key
    public static String decryptKey(String encryptedKey) {
        try {
            return decrypt(encryptedKey, "1234567890123456"); // Example secret key, should match the one used in encryption
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }
}

