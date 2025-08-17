package com.ecommerce.auth_service.service;

import com.ecommerce.auth_service.entity.User;
import com.ecommerce.auth_service.entity.VerificationCode;
import com.ecommerce.auth_service.model.request.RegistrationUserRequest;

import java.util.List;

public interface UserService {
    void register(RegistrationUserRequest registrationUserRequest);
    void confirmVerification(VerificationCode verificationCode);
    User findById(Long id);
    List<User> findAll();
    void deactivate(Long id);
}
