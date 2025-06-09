package com.ecommerce.auth_service.common.exception;

import com.ecommerce.auth_service.common.constant.GeneralError;
import com.ecommerce.auth_service.model.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class MainExceptionHandler {

    @ExceptionHandler(value = MainException.class)
    public ResponseEntity<BaseResponse> handleMainException(MainException exception) {
        BaseResponse response = new BaseResponse();
        response.setCode(exception.getCode());
        response.setMessage(exception.getMessage());
        response.setData(null);
        log.warn(String.format("validation code : %s , message : %s ", response.getCode(), response.getMessage()));

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ServletRequestBindingException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ResponseEntity<BaseResponse> servletRequestBindingException(ServletRequestBindingException exception) {
        BaseResponse response = new BaseResponse(GeneralError.VALIDATION_ERROR.getCode(), exception.getMessage(), null, null);
        log.warn(String.format("validation code : %s , message : %s ", response.getCode(), response.getMessage()));

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse> handleValidationExceptions(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.warn(String.format("400-VALIDATION : %s ", errors));

        return new ResponseEntity<>(new BaseResponse(GeneralError.VALIDATION_ERROR.getCode(), null, null, errors), HttpStatus.BAD_REQUEST);
    }
}
