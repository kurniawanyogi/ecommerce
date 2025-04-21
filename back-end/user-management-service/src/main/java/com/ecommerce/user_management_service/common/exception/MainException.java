package com.ecommerce.user_management_service.common.exception;

import lombok.Getter;

@Getter
public class MainException extends RuntimeException {
    private final String code;
    private final String message;

    public MainException(String code, String errorMessage) {
        this.code = code;
        this.message = errorMessage;
    }
}
