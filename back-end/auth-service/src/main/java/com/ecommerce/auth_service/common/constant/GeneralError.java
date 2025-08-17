package com.ecommerce.auth_service.common.constant;

import lombok.Getter;

@Getter
public enum GeneralError {
    VALIDATION_ERROR("VALIDATION_ERROR"),
    NOT_FOUND("NOT_FOUND"),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR");

    private final String code;
    GeneralError(String code) {
        this.code = code;
    }
}
