package com.ecommerce.auth_service.service;

import com.ecommerce.auth_service.entity.Permission;

import java.util.List;

public interface RolePermissionService {
    void assignPermissions(Long roleId, List<Long> permissionId);
    List<Permission> findPermissionsByRoleIds(List<Long> roleIds);
}