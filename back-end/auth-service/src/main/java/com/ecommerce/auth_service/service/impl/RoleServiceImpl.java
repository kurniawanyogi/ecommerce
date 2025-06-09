package com.ecommerce.auth_service.service.impl;

import com.ecommerce.auth_service.common.constant.GeneralError;
import com.ecommerce.auth_service.common.constant.GeneralStatus;
import com.ecommerce.auth_service.common.exception.MainException;
import com.ecommerce.auth_service.entity.Role;
import com.ecommerce.auth_service.model.request.SaveRoleRequest;
import com.ecommerce.auth_service.repository.RoleRepository;
import com.ecommerce.auth_service.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    public void saveRole(SaveRoleRequest request) {
        if (roleRepository.existsRoleByName(request.getName())) {
            throw new MainException(GeneralError.VALIDATION_ERROR.getCode(), "Role name already exists");
        }
        Role role = createRoleFromRequest(request, GeneralStatus.ACTIVE.getValue());
        roleRepository.save(role);
    }

    private Role createRoleFromRequest(SaveRoleRequest request, String status) {
        GeneralStatus generalStatus = validateStatus(status);
        Role role = new Role();
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        role.setStatus(generalStatus.getValue());
        return role;
    }

    private static GeneralStatus validateStatus(String status) {
        GeneralStatus generalStatus = GeneralStatus.fromString(status);
        if (generalStatus == null) {
            throw new MainException(GeneralError.VALIDATION_ERROR.getCode(), "Invalid status");
        }
        return generalStatus;
    }
}
