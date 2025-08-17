package com.ecommerce.auth_service.service;

import com.ecommerce.auth_service.common.constant.VerificationCodeType;
import com.ecommerce.auth_service.entity.VerificationCode;

public interface VerificationCodeService {
    String generateCode(VerificationCodeType type);

    boolean verifyCode(String code, VerificationCodeType type);
}
