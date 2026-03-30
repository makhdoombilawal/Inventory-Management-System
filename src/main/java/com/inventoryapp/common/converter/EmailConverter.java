package com.inventoryapp.common.converter;

import com.inventoryapp.common.security.AesUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
@Component
@RequiredArgsConstructor
public class EmailConverter implements AttributeConverter<String, String> {
    
    private final AesUtil aesUtil;

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) return null;
        try {
            return aesUtil.encrypt(attribute);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt email", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            return aesUtil.decrypt(dbData);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt email", e);
        }
    }
}
