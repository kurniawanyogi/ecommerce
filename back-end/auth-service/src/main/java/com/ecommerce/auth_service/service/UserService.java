package com.ecommerce.auth_service.service;

import com.ecommerce.auth_service.model.request.RegistrationUserRequest;

public interface UserService {
    void registerUser(RegistrationUserRequest request);
}
