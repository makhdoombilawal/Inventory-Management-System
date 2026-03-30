package com.inventoryapp.common.security.config;

import com.inventoryapp.common.security.AesPasswordEncoder;
import com.inventoryapp.common.security.AesUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Lightweight configuration for encryption-related beans.
 * Separated from main AuthConfig to prevent circular dependencies with JPA.
 */
@Configuration
public class EncryptionConfig {

    @Value("${aes.encryption.key}")
    private String aesKey;

    @Bean
    public AesUtil aesUtil() {
        return new AesUtil(aesKey);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new AesPasswordEncoder(aesUtil());
    }
}
