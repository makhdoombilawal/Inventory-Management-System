package com.inventoryapp.common.security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES/GCM/NoPadding encryption utility for securing sensitive data
 */
public class AesUtil {
    
    private static final int KEY_SIZE = 128;
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    
    private final SecretKey secretKey;
    
    /**
     * Initialize AES utility with base64 encoded key
     */
    public AesUtil(String encodedKey) {
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        this.secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }
    
    /**
     * Initialize AES utility with raw key bytes
     */
    public AesUtil(byte[] key) {
        this.secretKey = new SecretKeySpec(key, 0, key.length, "AES");
    }
    
    /**
     * Encrypt plaintext using AES/GCM/NoPadding
     */
    public String encrypt(String plaintext) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        byte[] iv = generateIV();
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);
        
        byte[] plainBytes = plaintext.getBytes(StandardCharsets.UTF_8);
        byte[] cipherBytes = cipher.doFinal(plainBytes);
        
        // Concatenate IV and ciphertext
        ByteBuffer buffer = ByteBuffer.allocate(iv.length + cipherBytes.length);
        buffer.put(iv);
        buffer.put(cipherBytes);
        
        return Base64.getEncoder().encodeToString(buffer.array());
    }
    
    /**
     * Decrypt ciphertext using AES/GCM/NoPadding
     */
    public String decrypt(String ciphertext) throws Exception {
        byte[] buffer = Base64.getDecoder().decode(ciphertext);
        
        // Extract IV
        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
        byte[] iv = new byte[GCM_IV_LENGTH];
        byteBuffer.get(iv);
        
        byte[] cipherBytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(cipherBytes);
        
        // Decrypt
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);
        
        byte[] plainBytes = cipher.doFinal(cipherBytes);
        return new String(plainBytes, StandardCharsets.UTF_8);
    }
    
    /**
     * Generate random IV for GCM mode
     */
    private byte[] generateIV() {
        byte[] iv = new byte[GCM_IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        return iv;
    }
    
    /**
     * Generate a new AES key
     */
    public static String generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(KEY_SIZE, new SecureRandom());
        SecretKey key = keyGen.generateKey();
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
}
