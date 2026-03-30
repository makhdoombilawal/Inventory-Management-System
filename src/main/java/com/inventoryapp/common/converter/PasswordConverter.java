package com.inventoryapp.common.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
@Component
public class PasswordConverter implements AttributeConverter<String, String> {
    
    private final com.inventoryapp.common.security.AesUtil aesUtil;

    @Autowired
    public PasswordConverter(@Lazy com.inventoryapp.common.security.AesUtil aesUtil) {
        this.aesUtil = aesUtil;
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) return null;
        try {
            return aesUtil.encrypt(attribute);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt password", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            return aesUtil.decrypt(dbData);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt password", e);
        }
    }
}
