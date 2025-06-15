package com.ecommerce.auth_service.service.impl;

import com.ecommerce.auth_service.entity.Role;
import com.ecommerce.auth_service.entity.User;
import com.ecommerce.auth_service.entity.UserRole;
import com.ecommerce.auth_service.entity.UserRoleId;
import com.ecommerce.auth_service.repository.UserRoleRepository;
import com.ecommerce.auth_service.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {
    private final UserRoleRepository userRoleRepository;

    @Override
    public UserRole saveUserRole(User user, Role role) {
        UserRole userRole = new UserRole(user, role);
        return userRoleRepository.save(userRole);
    }

    @Override
    public List<UserRole> findByUser(User user) {
        return userRoleRepository.findByUser(user);
    }

    @Override
    public List<UserRole> findByRole(Role role) {
        return userRoleRepository.findByRole(role);
    }

    @Override
    public Optional<UserRole> findById(UserRoleId id) {
        return userRoleRepository.findById(id);
    }

    @Override
    public void deleteUserRole(UserRole userRole) {
        userRoleRepository.delete(userRole);
    }
}
