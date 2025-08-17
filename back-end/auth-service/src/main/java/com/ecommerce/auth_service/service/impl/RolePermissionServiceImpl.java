package com.ecommerce.auth_service.service.impl;

import com.ecommerce.auth_service.common.constant.GeneralError;
import com.ecommerce.auth_service.common.constant.GeneralStatus;
import com.ecommerce.auth_service.common.exception.MainException;
import com.ecommerce.auth_service.entity.Permission;
import com.ecommerce.auth_service.entity.Role;
import com.ecommerce.auth_service.entity.RolePermission;
import com.ecommerce.auth_service.entity.RolePermissionId;
import com.ecommerce.auth_service.repository.PermissionRepository;
import com.ecommerce.auth_service.repository.RolePermissionRepository;
import com.ecommerce.auth_service.repository.RoleRepository;
import com.ecommerce.auth_service.service.RolePermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RolePermissionServiceImpl implements RolePermissionService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    @Transactional
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        Role role = getActiveRole(roleId);
        Set<Long> requestedIds = new HashSet<>(permissionIds);
        Set<Long> existingIds = getExistingPermissionIds(roleId);

        removeUnassignedPermissions(roleId, existingIds, requestedIds);
        addNewPermissions(role, existingIds, requestedIds);
    }

    @Override
    public List<Permission> findPermissionsByRoleIds(List<Long> roleIds) {
        List<RolePermission> rolePermissions = rolePermissionRepository.findByRole_IdIn(roleIds);
        return rolePermissions.stream()
                .map(RolePermission::getPermission)
                .collect(Collectors.toList());
    }

    private Role getActiveRole(Long id) {
        Role existingRole = roleRepository.findRoleByIdAndStatus(id, GeneralStatus.ACTIVE.getValue());
        if (existingRole == null) {
            throw new MainException(GeneralError.VALIDATION_ERROR.getCode(), "Role not found");
        }
        return existingRole;
    }

    private Permission getActivePermission(Long id) {
        Permission existingPermission = permissionRepository.findByIdAndStatus(id, GeneralStatus.ACTIVE.getValue());
        if (existingPermission == null) {
            throw new MainException(GeneralError.VALIDATION_ERROR.getCode(), "Permission not found");
        }
        return existingPermission;
    }

    private Set<Long> getExistingPermissionIds(Long roleId) {
        return rolePermissionRepository.findAllByRoleId(roleId).stream()
                .map(rp -> rp.getPermission().getId())
                .collect(Collectors.toSet());
    }

    private void removeUnassignedPermissions(Long roleId, Set<Long> existingIds, Set<Long> requestedIds) {
        Set<Long> idsToRemove = new HashSet<>(existingIds);
        idsToRemove.removeAll(requestedIds);
        for (Long permissionId : idsToRemove) {
            RolePermissionId id = new RolePermissionId(roleId, permissionId);
            rolePermissionRepository.deleteById(id);
        }
    }

    private void addNewPermissions(Role role, Set<Long> existingIds, Set<Long> requestedIds) {
        Set<Long> idsToAdd = new HashSet<>(requestedIds);
        idsToAdd.removeAll(existingIds);

        for (Long permissionId : idsToAdd) {
            Permission permission = getActivePermission(permissionId);
            RolePermission rolePermission = RolePermission.builder()
                    .id(new RolePermissionId(role.getId(), permissionId))
                    .role(role)
                    .permission(permission)
                    .build();
            rolePermissionRepository.save(rolePermission);
        }
    }
}
