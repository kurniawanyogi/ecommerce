package com.ecommerce.user_management_service.controller;

import com.ecommerce.user_management_service.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for role operations
 * Handles: CRUD role, assign roles to users (optional in initial phase)
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/roles")
public class RoleController {
    private final RoleService roleService;
}
