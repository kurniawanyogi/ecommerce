package com.ecommerce.auth_service.repository;

import com.ecommerce.auth_service.entity.RolePermission;
import com.ecommerce.auth_service.entity.RolePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermissionId> {
    List<RolePermission> findAllByRoleId(Long roleId);
}
