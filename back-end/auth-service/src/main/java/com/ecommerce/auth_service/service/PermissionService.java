package com.ecommerce.auth_service.service;

import com.ecommerce.auth_service.entity.Permission;
import com.ecommerce.auth_service.entity.Role;
import com.ecommerce.auth_service.model.request.SavePermissionRequest;

import java.util.List;

public interface PermissionService {
    List<Permission> findPermissions();
    Permission findPermission(Long id);
    void savePermission(SavePermissionRequest request);
    void updatePermission(SavePermissionRequest request, Long id);
    void deletePermission(Long id);
}
