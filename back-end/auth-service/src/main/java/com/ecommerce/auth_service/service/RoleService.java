package com.ecommerce.auth_service.service;

import com.ecommerce.auth_service.entity.Role;
import com.ecommerce.auth_service.model.request.SaveRoleRequest;

import java.util.List;

public interface RoleService {
    List<Role> findRoles();
    Role findRole(Long id);
    void saveRole(SaveRoleRequest request);
    void updateRole(SaveRoleRequest request, Long id);
    void deleteRole(Long id);
    Role findRoleByName(String name);
}
