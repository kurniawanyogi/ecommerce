package com.ecommerce.user_management_service.repository;

import com.ecommerce.user_management_service.entity.UserRole;
import com.ecommerce.user_management_service.entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
}