package com.ecommerce.user_management_service.service.impl;

import com.ecommerce.user_management_service.common.constant.ROLE;
import com.ecommerce.user_management_service.common.constant.STATUS;
import com.ecommerce.user_management_service.common.exception.MainException;
import com.ecommerce.user_management_service.entity.Customer;
import com.ecommerce.user_management_service.entity.Role;
import com.ecommerce.user_management_service.entity.User;
import com.ecommerce.user_management_service.entity.UserRole;
import com.ecommerce.user_management_service.model.request.CustomerRegistrationRequest;
import com.ecommerce.user_management_service.repository.*;
import com.ecommerce.user_management_service.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;

/**
 * Implementation of CustomerService
 * Handles customer operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public void registerCustomer(CustomerRegistrationRequest payload) {
        User existingEmail = userRepository.findByEmail(payload.getEmail());
        if (existingEmail != null) throw new MainException("400-VALIDATION", "Email already taken");

        User existingPhone = userRepository.findByPhoneNumber(payload.getPhoneNumber());
        if (existingPhone != null) throw new MainException("400-VALIDATION", "Phone Number already taken");

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        User user = new User();
        user.setStatus(STATUS.INACTIVE.getName());
        user.setEmail(payload.getEmail());
        user.setPasswordHash(encoder.encode(payload.getPassword()));
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        user.setVersion(0L);
        User savedUser = userRepository.save(user);

        Customer customer = new Customer();
        customer.setUser(savedUser);
        customer.setAddress(payload.getAddress());
        customer.setVersion(0L);
        customer.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        customer.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        customer.setFullName(payload.getFullName());
        //TODO
        customer.setBirthDate(null);
        customer.setProfilePicture(null);
        customerRepository.save(customer);

        Role customerRole = roleRepository.findByName(ROLE.CUSTOMER.getName());
        if (customerRole == null) throw new MainException("500-INTERNAL", "Customer role not found");
        userRoleRepository.save(new UserRole(savedUser, customerRole));
    }

}