package com.ecommerce.auth_service.controller;

import com.ecommerce.auth_service.common.constant.GeneralError;
import com.ecommerce.auth_service.common.constant.VerificationCodeType;
import com.ecommerce.auth_service.common.exception.MainException;
import com.ecommerce.auth_service.entity.VerificationCode;
import com.ecommerce.auth_service.model.request.LoginRequest;
import com.ecommerce.auth_service.model.response.BaseResponse;
import com.ecommerce.auth_service.model.response.LoginResponse;
import com.ecommerce.auth_service.service.AuthService;
import com.ecommerce.auth_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final AuthService authService;

    @GetMapping(value = "/register/confirm")
    public ResponseEntity<BaseResponse> confirmVerification(
            @RequestParam("code") String code,
            @RequestParam("type") VerificationCodeType type) {

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setCode(code);
        verificationCode.setType(type);

        userService.confirmVerification(verificationCode);
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.toString(), "Verification successful", null, null));
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = new LoginResponse();
        try {
            response = authService.login(request);
        } catch (MainException ex) {
            if (ex.getCode().equals(GeneralError.UNAUTHENTICATED.getCode())) {
                return ResponseEntity.ok(
                        new BaseResponse(HttpStatus.UNAUTHORIZED.toString(), ex.getMessage(), null, null)
                );
            } else {
                return ResponseEntity.ok(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.toString(), ex.getMessage(), null, null));
            }
        }
        return ResponseEntity.ok(
                new BaseResponse(HttpStatus.OK.toString(), "Login successful", response, null)
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<BaseResponse> logout(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        authService.logout(token);
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.toString(), "Logout successful", null, null));
    }
}
