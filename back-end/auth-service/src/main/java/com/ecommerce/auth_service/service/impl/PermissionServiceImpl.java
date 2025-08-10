package com.ecommerce.auth_service.service.impl;

import com.ecommerce.auth_service.common.constant.GeneralError;
import com.ecommerce.auth_service.common.constant.GeneralStatus;
import com.ecommerce.auth_service.common.exception.MainException;
import com.ecommerce.auth_service.entity.Permission;
import com.ecommerce.auth_service.entity.Role;
import com.ecommerce.auth_service.model.request.SavePermissionRequest;
import com.ecommerce.auth_service.model.request.SaveRoleRequest;
import com.ecommerce.auth_service.repository.PermissionRepository;
import com.ecommerce.auth_service.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;

    @Override
    public List<Permission> findPermissions() {
        return permissionRepository.findAll();
    }

    @Override
    public Permission findPermission(Long id) {
        return getActivePermission(id);
    }

    @Override
    public void savePermission(SavePermissionRequest request) {
        validateName(request.getName());
        Permission permission = createPermissionFromRequest(request, GeneralStatus.ACTIVE.getValue());
        permissionRepository.save(permission);
    }

    private void validateName(String name) {
        if (permissionRepository.existsByName(name)) {
            throw new MainException(GeneralError.VALIDATION_ERROR.getCode(), "Permission name already exists");
        }
    }

    private Permission createPermissionFromRequest(SavePermissionRequest request, String status) {
        GeneralStatus generalStatus = validateStatus(status);
        Permission permission = new Permission();
        permission.setName(request.getName());
        permission.setDescription(request.getDescription());
        permission.setStatus(generalStatus.getValue());
        return permission;
    }

    private static GeneralStatus validateStatus(String status) {
        GeneralStatus generalStatus = GeneralStatus.fromString(status);
        if (generalStatus == null) {
            throw new MainException(GeneralError.VALIDATION_ERROR.getCode(), "Invalid status");
        }
        return generalStatus;
    }

    @Override
    public void updatePermission(SavePermissionRequest request, Long id) {
        Permission existingPermission = getActivePermission(id);
        if (!existingPermission.getName().equalsIgnoreCase(request.getName())) {
            validateName(request.getName());
        }
        existingPermission.setName(request.getName());
        existingPermission.setDescription(request.getDescription());
        permissionRepository.save(existingPermission);
    }

    private Permission getActivePermission(Long id) {
        Permission existingPermission = permissionRepository.findByIdAndStatus(id, GeneralStatus.ACTIVE.getValue());
        if (existingPermission == null) {
            throw new MainException(GeneralError.VALIDATION_ERROR.getCode(), "Permission not found");
        }
        return existingPermission;
    }

    @Override
    public void deletePermission(Long id) {
        Permission existingPermission = getActivePermission(id);
        existingPermission.setStatus(GeneralStatus.INACTIVE.getValue());
        permissionRepository.save(existingPermission);
    }
}
