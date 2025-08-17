package com.ecommerce.auth_service.service.impl;

import com.ecommerce.auth_service.common.constant.GeneralError;
import com.ecommerce.auth_service.common.constant.VerificationCodeType;
import com.ecommerce.auth_service.common.exception.MainException;
import com.ecommerce.auth_service.entity.VerificationCode;
import com.ecommerce.auth_service.repository.VerificationCodeRepository;
import com.ecommerce.auth_service.service.VerificationCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class VerificationCodeServiceImpl implements VerificationCodeService {
    private final VerificationCodeRepository verificationCodeRepository;

    private final long EXPIRED_IN_MINUTES = 60 * 1000;

    @Override
    public String generateCode(VerificationCodeType type, long userId) {
        int maxRetries = 5;
        int attempt = 0;
        String code;
        do {
            if (attempt >= maxRetries) {
                throw new MainException(GeneralError.INTERNAL_SERVER_ERROR.getCode(), "Failed to generate unique code in " + maxRetries + " attempts");
            }
            code = String.format("%06d", new SecureRandom().nextInt(1_000_000));
            attempt++;
        } while (verificationCodeRepository.existsByTypeAndCodeAndVerifiedFalse(type, code));

        Timestamp now = new Timestamp(System.currentTimeMillis());
        Timestamp expiresAt = new Timestamp(now.getTime() + (5 * EXPIRED_IN_MINUTES));

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setCode(code);
        verificationCode.setType(type);
        verificationCode.setUserId(userId);
        verificationCode.setExpiresAt(expiresAt);
        verificationCode.setCreatedAt(now);
        verificationCode.setUpdatedAt(now);
        verificationCode.setVerified(false);

        verificationCodeRepository.save(verificationCode);
        return code;
    }

    @Override
    public VerificationCode verifyCode(String code, VerificationCodeType type) {
        Timestamp now = new Timestamp(System.currentTimeMillis());

        VerificationCode verificationCode = verificationCodeRepository
                .findByCodeAndTypeAndVerifiedFalse(code, type);

        if (verificationCode == null) {
            return null;
        }
        if (verificationCode.getExpiresAt().before(now)) {
            return null;
        }

        verificationCode.setVerified(true);
        verificationCode.setUpdatedAt(now);

        verificationCodeRepository.save(verificationCode);

        return verificationCode;
    }
}
