package com.ecommerce.auth_service.service;

import com.ecommerce.auth_service.entity.Role;
import com.ecommerce.auth_service.entity.User;
import com.ecommerce.auth_service.entity.UserRole;
import com.ecommerce.auth_service.entity.UserRoleId;

import java.util.List;
import java.util.Optional;

public interface UserRoleService {
    UserRole saveUserRole(User user, Role role);

    List<UserRole> findByUser(User user);

    List<UserRole> findByRole(Role role);

    Optional<UserRole> findById(UserRoleId id);

    void deleteUserRole(UserRole userRole);
}