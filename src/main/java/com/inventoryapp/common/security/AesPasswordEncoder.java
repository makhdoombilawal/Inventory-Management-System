package com.inventoryapp.common.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
/**
 * Custom PasswordEncoder using AES/GCM encryption.
 */
@RequiredArgsConstructor
public class AesPasswordEncoder implements PasswordEncoder {

    private final AesUtil aesUtil;

    @Override
    public String encode(CharSequence rawPassword) {
        try {
            return aesUtil.encrypt(rawPassword.toString());
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt password", e);
        }
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        try {
            String decrypted = aesUtil.decrypt(encodedPassword);
            return rawPassword.toString().equals(decrypted);
        } catch (Exception e) {
            return false;
        }
    }
}
