package com.ecommerce.user_management_service.controller;

import com.ecommerce.user_management_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for authentication operations
 * Handles: Register, Login, Resend OTP, Forgot/Change Password
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
}
