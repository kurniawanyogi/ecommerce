package com.ecommerce.user_management_service.repository;

import com.ecommerce.user_management_service.entity.RolePermission;
import com.ecommerce.user_management_service.entity.RolePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermissionId> {
}