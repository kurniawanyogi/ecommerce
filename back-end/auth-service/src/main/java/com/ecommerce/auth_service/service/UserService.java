package com.ecommerce.auth_service.service;

import com.ecommerce.auth_service.entity.User;
import com.ecommerce.auth_service.entity.VerificationCode;
import com.ecommerce.auth_service.model.request.RegistrationUserRequest;
import com.ecommerce.auth_service.model.request.UpdateUserRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {
    void register(RegistrationUserRequest registrationUserRequest);
    void confirmVerification(VerificationCode verificationCode);
    User findById(Long id);
    Page<User> findAll(int page, int size);
    void deactivate(Long id);
    void requestUserReactivation(Long id);
    void updateUser(Long id, UpdateUserRequest request);
}
