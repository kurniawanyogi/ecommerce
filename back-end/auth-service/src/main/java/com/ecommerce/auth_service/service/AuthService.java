package com.ecommerce.auth_service.service;

import com.ecommerce.auth_service.model.request.LoginRequest;
import com.ecommerce.auth_service.model.response.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest);
    void logout(String token);
    void validateToken(String token);
}
