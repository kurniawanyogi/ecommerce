package com.ecommerce.auth_service.repository;

import com.ecommerce.auth_service.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    boolean existsRoleByName(String name);
    Role findRoleByIdAndStatus(Long id, String status);
    Role findRoleByName(String name);
}
