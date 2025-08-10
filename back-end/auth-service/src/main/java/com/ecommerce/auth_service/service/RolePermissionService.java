package com.ecommerce.auth_service.service;

import java.util.List;

public interface RolePermissionService {
    void assignPermissions(Long roleId, List<Long> permissionId);
}