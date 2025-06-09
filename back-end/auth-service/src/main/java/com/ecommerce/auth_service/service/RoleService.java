package com.ecommerce.auth_service.service;

import com.ecommerce.auth_service.model.request.SaveRoleRequest;

public interface RoleService {
    void saveRole(SaveRoleRequest request);
    void updateRole(SaveRoleRequest request, Long id);
    void deleteRole(Long id);
}
