package com.ecommerce.auth_service.service;

import com.ecommerce.auth_service.entity.User;
import com.ecommerce.auth_service.model.request.RegistrationUserRequest;

public interface UserService {
    void register(RegistrationUserRequest registrationUserRequest);
    User findById(Long id);
}
