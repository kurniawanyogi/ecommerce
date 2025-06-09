package com.ecommerce.auth_service.service.impl;

import com.ecommerce.auth_service.model.request.RegistrationUserRequest;
import com.ecommerce.auth_service.repository.UserRepository;
import com.ecommerce.auth_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleServiceImpl roleService;


    @Override
    public void registerUser(RegistrationUserRequest request) {
      //TODO implement logic for user registration
    }

    //TODO implement logic for user login and save to redis
}
