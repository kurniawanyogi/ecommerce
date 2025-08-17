package com.ecommerce.auth_service.service;

import com.ecommerce.auth_service.common.constant.VerificationCodeType;
import com.ecommerce.auth_service.common.exception.MainException;
import com.ecommerce.auth_service.entity.VerificationCode;
import com.ecommerce.auth_service.repository.VerificationCodeRepository;
import com.ecommerce.auth_service.service.impl.VerificationCodeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VerificationCodeServiceImplTest {
    @Mock
    private VerificationCodeRepository verificationCodeRepository;

    @InjectMocks
    private VerificationCodeServiceImpl verificationCodeService;

    @Test
    void shouldGenerateUniqueCodeSuccessfully() {
        VerificationCodeType type = VerificationCodeType.REGISTRATION;

        when(verificationCodeRepository.existsByTypeAndCodeAndVerifiedFalse(eq(type), anyString()))
                .thenReturn(false);

        String code = verificationCodeService.generateCode(type);

        assertNotNull(code);
        assertEquals(6, code.length());
        verify(verificationCodeRepository, times(1)).existsByTypeAndCodeAndVerifiedFalse(eq(type), anyString());
        verify(verificationCodeRepository, times(1)).save(any(VerificationCode.class));
    }

    @Test
    void shouldThrowException_whenMaxRetriesExceeded() {
        VerificationCodeType type = VerificationCodeType.REGISTRATION;

        when(verificationCodeRepository.existsByTypeAndCodeAndVerifiedFalse(eq(type), anyString()))
                .thenReturn(true);

        MainException ex = assertThrows(MainException.class, () -> verificationCodeService.generateCode(type));

        assertTrue(ex.getMessage().contains("Failed to generate unique code"));
        verify(verificationCodeRepository, atLeast(5)).existsByTypeAndCodeAndVerifiedFalse(eq(type), anyString());
        verify(verificationCodeRepository, never()).save(any());
    }

    @Test
    void shouldReturnTrue_whenCodeIsValidAndNotExpired() {
        VerificationCodeType type = VerificationCodeType.REGISTRATION;
        String code = "123456";
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Timestamp expiresAt = new Timestamp(now.getTime() + 60000);

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setCode(code);
        verificationCode.setType(type);
        verificationCode.setExpiresAt(expiresAt);
        verificationCode.setVerified(false);

        when(verificationCodeRepository.findByCodeAndTypeAndVerifiedFalse(code, type))
                .thenReturn(verificationCode);

        boolean result = verificationCodeService.verifyCode(code, type);

        assertTrue(result);
        assertTrue(verificationCode.isVerified());
        verify(verificationCodeRepository, times(1)).findByCodeAndTypeAndVerifiedFalse(code, type);
        verify(verificationCodeRepository, times(1)).save(verificationCode);
    }

    @Test
    void shouldReturnFalse_whenCodeNotFound() {
        VerificationCodeType type = VerificationCodeType.REGISTRATION;
        String code = "654321";

        when(verificationCodeRepository.findByCodeAndTypeAndVerifiedFalse(code, type))
                .thenReturn(null);

        boolean result = verificationCodeService.verifyCode(code, type);

        assertFalse(result);
        verify(verificationCodeRepository, times(1)).findByCodeAndTypeAndVerifiedFalse(code, type);
        verify(verificationCodeRepository, never()).save(any());
    }

    @Test
    void shouldReturnFalse_whenCodeExpired() {
        VerificationCodeType type = VerificationCodeType.REGISTRATION;
        String code = "999999";

        Timestamp now = new Timestamp(System.currentTimeMillis());
        Timestamp expiredTime = new Timestamp(now.getTime() - 1000);

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setCode(code);
        verificationCode.setType(type);
        verificationCode.setExpiresAt(expiredTime);
        verificationCode.setVerified(false);

        when(verificationCodeRepository.findByCodeAndTypeAndVerifiedFalse(code, type))
                .thenReturn(verificationCode);

        boolean result = verificationCodeService.verifyCode(code, type);

        assertFalse(result);
        verify(verificationCodeRepository, times(1)).findByCodeAndTypeAndVerifiedFalse(code, type);
        verify(verificationCodeRepository, never()).save(any());
    }
}
