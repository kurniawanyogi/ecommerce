package com.ecommerce.auth_service.common.constant;

import lombok.Getter;

@Getter
public enum GeneralError {
    VALIDATION_ERROR("VALIDATION_ERROR");

    private final String code;
    GeneralError(String code) {
        this.code = code;
    }
}
