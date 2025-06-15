package com.ecommerce.auth_service.service.impl;

import com.ecommerce.auth_service.common.constant.RoleEnum;
import com.ecommerce.auth_service.entity.Role;
import com.ecommerce.auth_service.model.request.AdminRegistrationRequest;
import com.ecommerce.auth_service.model.request.RegistrationUserRequest;
import com.ecommerce.auth_service.model.request.SellerRegistrationRequest;
import com.ecommerce.auth_service.entity.User;
import com.ecommerce.auth_service.repository.UserRepository;
import com.ecommerce.auth_service.service.UserRoleService;
import com.ecommerce.auth_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleServiceImpl roleService;
    private final UserRoleService userRoleService;
    private final BCryptPasswordEncoder passwordEncoder;


    public void registerSeller(SellerRegistrationRequest request) {
        validatePasswordMatch(request);
        User user = mapToUser(request);
        user = userRepository.save(user);

        Role sellerRole = roleService.findRoleByName(RoleEnum.SELLER.name());
        if (sellerRole != null) {
            userRoleService.saveUserRole(user, sellerRole);
        }
    }

    public void registerAdmin(AdminRegistrationRequest request) {
        validatePasswordMatch(request);
        User user = mapToUser(request);
        user = userRepository.save(user);

        Role adminRole = roleService.findRoleByName(RoleEnum.ADMIN.name());
        if (adminRole != null) {
            userRoleService.saveUserRole(user, adminRole);
        }
    }

    private void validatePasswordMatch(RegistrationUserRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Password and confirm password do not match");
        }
    }

    private User mapToUser(RegistrationUserRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        if (request.getBirthDate() != null) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                user.setBirthDate(formatter.parse(request.getBirthDate()));
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid date format. Please use dd-MM-yyyy format");
            }
        }
        user.setGender(request.getGender());
        return user;
    }
}
