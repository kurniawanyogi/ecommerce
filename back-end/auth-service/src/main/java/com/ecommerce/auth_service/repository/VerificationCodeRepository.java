package com.ecommerce.auth_service.repository;

import com.ecommerce.auth_service.common.constant.VerificationCodeType;
import com.ecommerce.auth_service.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    boolean existsByTypeAndCodeAndVerifiedFalse(VerificationCodeType type, String code);
    VerificationCode findByCodeAndTypeAndVerifiedFalse(String code, VerificationCodeType type);
}
